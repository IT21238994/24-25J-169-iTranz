package com.example.itranz.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PaymentRequestActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_request)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val etNIC = findViewById<EditText>(R.id.etNIC)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnSendRequest = findViewById<Button>(R.id.btnSendRequest)

        btnSendRequest.setOnClickListener {
            val nic = etNIC.text.toString()
            val amount = etAmount.text.toString().toDoubleOrNull()

            if (nic.isEmpty() || amount == null || amount <= 0) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users").whereEqualTo("nic", nic).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val passengerDoc = documents.documents[0]
                        val receiverId = passengerDoc.id
                        val fcmToken = passengerDoc.getString("fcmToken")

                        val paymentRequest = hashMapOf(
                            "senderId" to auth.currentUser!!.uid,
                            "receiverId" to receiverId,
                            "amount" to amount,
                            "status" to "pending",
                            "timestamp" to System.currentTimeMillis()
                        )

                        db.collection("payments").add(paymentRequest)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Payment Request Sent!", Toast.LENGTH_SHORT).show()
                                fcmToken?.let { sendNotification(it, "New Payment Request", "Rs. $amount requested.") }
                            }
                    } else {
                        Toast.makeText(this, "Passenger not found!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun sendNotification(token: String, title: String, message: String) {
        val url = "https://fcm.googleapis.com/fcm/send"
        val json = JSONObject()
        val notification = JSONObject()

        notification.put("title", title)
        notification.put("body", message)
        json.put("to", token)
        json.put("notification", notification)

        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "key=YOUR_FCM_SERVER_KEY")
            .header("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "Notification sending failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("FCM", "Notification sent: ${response.body()?.string()}")
            }
        })
    }
}
