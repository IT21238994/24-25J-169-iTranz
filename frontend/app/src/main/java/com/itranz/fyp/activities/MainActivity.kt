package com.itranz.fyp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import java.io.IOException
import com.itranz.fyp.R

class MainActivity : AppCompatActivity() {

    // Use 10.0.2.2 if running on Android Emulator
    private val backendUrl = "http://10.0.2.2:3000/saveToken"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchAndSendFCMToken()
    }

    private fun fetchAndSendFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMToken", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCMToken", token)

            val nic = "123456789V"  // Replace with actual NIC
            sendTokenToBackend(token, nic)
        }
    }

    private fun sendTokenToBackend(token: String, nic: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("nic", nic)
            .add("token", token)
            .build()

        val request = Request.Builder()
            .url(backendUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Token send failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Token sent!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
