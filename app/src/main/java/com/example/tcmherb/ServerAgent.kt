package com.example.tcmherb

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ServerAgent {
    private val helloWorldURL = URL("https://herb-server-mj26pnawdq-uc.a.run.app/")

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
}