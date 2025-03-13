package com.itranz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NICPaymentActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val fcmServerKey = "YOUR_FCM_SERVER_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nic_payment)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val etNIC = findViewById<EditText>(R.id.etNIC)
        val etStartStop = findViewById<EditText>(R.id.etStartStop)
        val etEndStop = findViewById<EditText>(R.id.etEndStop)
        val btnCalculateFare = findViewById<Button>(R.id.btnCalculateFare)
        val btnPay = findViewById<Button>(R.id.btnPay)

        btnCalculateFare.setOnClickListener {
            val startStop = etStartStop.text.toString().toInt()
            val endStop = etEndStop.text.toString().toInt()

            if (endStop <= startStop) {
                Toast.makeText(this, "Invalid stops!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fare = calculateFare(startStop, endStop)
            Toast.makeText(this, "Calculated Fare: Rs. $fare", Toast.LENGTH_LONG).show()
        }

        btnPay.setOnClickListener {
            val nic = etNIC.text.toString()
            val startStop = etStartStop.text.toString().toInt()
            val endStop = etEndStop.text.toString().toInt()

            if (nic.isEmpty() || endStop <= startStop) {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fare = calculateFare(startStop, endStop)
            processPayment(nic, fare)
        }
    }

    private fun calculateFare(startStop: Int, endStop: Int): Double {
        val ratePerStop = 5.0  // Example: Rs. 5 per stop
        return (endStop - startStop) * ratePerStop
    }

    private fun processPayment(nic: String, fare: Double) {
        db.collection("users").whereEqualTo("nic", nic).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val balance = doc.getDouble("balance") ?: 0.0
                    val fcmToken = doc.getString("fcmToken") ?: ""

                    if (fcmToken.isEmpty()) {
                        Toast.makeText(this, "FCM token not found!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    if (balance < fare) {
                        Toast.makeText(this, "Insufficient balance!", Toast.LENGTH_SHORT).show()
                    } else {
                        sendPaymentNotification(fcmToken, fare)
                        storePendingPayment(nic, fare)
                    }
                } else {
                    Toast.makeText(this, "NIC not found!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendPaymentNotification(fcmToken: String, fare: Double) {
        val client = OkHttpClient()
        val json = JSONObject()
        val notification = JSONObject()
        val data = JSONObject()

        try {
            notification.put("title", "Payment Request")
            notification.put("body", "Approve Rs. $fare payment?")
            data.put("fare", fare)

            json.put("to", fcmToken)
            json.put("notification", notification)
            json.put("data", data)

            val body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toString())
            val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=$fcmServerKey")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Failed to send notification", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Payment request sent!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun storePendingPayment(nic: String, fare: Double) {
        val paymentData = hashMapOf(
            "nic" to nic,
            "fare" to fare,
            "status" to "pending"
        )

        db.collection("pending_payments").add(paymentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Payment request stored!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to store payment request", Toast.LENGTH_SHORT).show()
            }
    }
}
