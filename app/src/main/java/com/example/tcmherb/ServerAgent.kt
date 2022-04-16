package com.example.tcmherb

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.Core.split
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.math.min


class ServerAgent {
    private val indexToXIndex = intArrayOf(1,10,11,12,13,14,15,16,17,18,19,2,20,21,22,23,24,25,26,27,28,29,3,30,31,32,33,34,35,36,37,38,39,4,40,41,5,6,7,8,9)

    private val helloWorldURL = URL("https://herb-server-mj26pnawdq-uc.a.run.app/")
    private val testURL = URL("https://herb-server-mj26pnawdq-uc.a.run.app/test")

    fun helloWorld(){
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build()

        try {
            val request: Request = Request.Builder()
                .url(helloWorldURL)
                .build()
            val result = client.newCall(request).execute()
            result.body?.let { Log.d("Connection result", it.string()) }
        } catch (e: Exception) {
            Log.e("Connection Error","$e")
        }
    }

    fun testImage(bitmap: Bitmap) : ArrayList<Pair<Int,Double>>{
        val parsedResult = ArrayList<Pair<Int,Double>>()

        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build()
        try {
            Log.d("Connection", "Processing image")
            val width = bitmap.width
            val height = bitmap.height
            val widthIsSmaller = width < height
            val newSize = min(width, height)
            val newBitmap = Bitmap.createBitmap(
                bitmap,
                if(widthIsSmaller) 0 else (width-newSize)/2,
                if(widthIsSmaller) (height-newSize)/2 else 0,
                newSize,
                newSize
            )

            val byteArrayOS = ByteArrayOutputStream()
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS)
            val encodedBitmap = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)

            val jsonParam = JSONObject()
            jsonParam.put("image", encodedBitmap)
            val JSON = "application/json; charset=utf-8".toMediaType()
            val body: RequestBody = jsonParam.toString().toRequestBody(JSON)

            val request: Request = Request.Builder()
                .url(testURL)
                .post(body)
                .build()

            Log.d("Connection", "Sending and classifying image")
            val result = client.newCall(request).execute()

            Log.d("Connection", "Reading and parsing response")
            result.body?.let {
                val response = JSONObject(it.string())
                val index = response.getJSONArray("result index")
                val confidence = response.getJSONArray("result confidence")
                for (i in 0 until index.length()-1){
                    parsedResult.add(Pair(indexToXIndex[index.getInt(i)], confidence.getDouble(i)))
                }
            }
        } catch (e: Exception) {
            Log.e("Connection Error","$e")
        }

        if(parsedResult.isEmpty()) parsedResult.add(Pair(-1,0.0))
        return parsedResult
    }
}