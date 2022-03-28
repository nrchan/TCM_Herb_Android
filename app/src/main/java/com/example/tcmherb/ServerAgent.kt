package com.example.tcmherb

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class ServerAgent {
    val indexToXIndex = intArrayOf(1,10,11,12,13,14,15,16,17,18,19,2,20,21,22,23,24,25,26,27,28,29,3,30,31,32,33,34,35,36,37,38,39,4,40,41,5,6,7,8,9)

    private val helloWorldURL = URL("https://herb-server-mj26pnawdq-uc.a.run.app/")
    private val testURL = URL("https://herb-server-mj26pnawdq-uc.a.run.app/test")

    fun helloWorld(){
        val connection = helloWorldURL.openConnection() as HttpURLConnection
        try {
            Log.d("Connection started", "connection started")
            val rd = BufferedReader(InputStreamReader(connection.inputStream))
            var result = ""
            rd.use {
                it.lines().forEach {
                    result += it
                }
            }
            rd.close()
            Log.d("Connection result", result)
        } catch (e: Exception) {
            Log.e("Connection Error","$e")
        } finally {
            connection.disconnect()
        }
    }

    fun testImage(bitmap: Bitmap) : ArrayList<Pair<Int,Double>>{
        val parsedResult = ArrayList<Pair<Int,Double>>()

        val byteArrayOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS)
        val encodedBitmap = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)

        val connection = testURL.openConnection() as HttpURLConnection
        try {
            Log.d("Connection started", "image connection started")
            connection.doInput = true
            connection.doOutput = true
            connection.setRequestProperty("Content-type", "application/json")
            connection.setRequestProperty("Accept", "text/plain")
            connection.setChunkedStreamingMode(0)

            val jsonParam = JSONObject()
            jsonParam.put("image", encodedBitmap)

            val out = DataOutputStream(connection.outputStream)
            out.writeBytes(jsonParam.toString())
            out.flush()
            out.close()

            val rd = BufferedReader(InputStreamReader(connection.inputStream))
            var result = ""
            rd.use {
                it.lines().forEach {
                    result += it
                }
            }
            rd.close()

            val response = JSONObject(result)
            val index = response.getJSONArray("result index")
            val confidence = response.getJSONArray("result confidence")
            for (i in 0 until index.length()-1){
                parsedResult.add(Pair(indexToXIndex[index.getInt(i)], confidence.getDouble(i)))
            }

            Log.d("Connection result", result)
        } catch (e: Exception) {
            Log.e("Connection Error","$e")
        } finally {
            connection.disconnect()
        }
        return parsedResult
    }
}