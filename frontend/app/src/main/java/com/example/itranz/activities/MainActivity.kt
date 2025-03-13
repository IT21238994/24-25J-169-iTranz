package com.itranz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnScanQR = findViewById<Button>(R.id.btnScanQR)
        val btnPayment = findViewById<Button>(R.id.btnPayment)
        val btnReserveSeat = findViewById<Button>(R.id.btnReserveSeat)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnScanQR.setOnClickListener {
            startActivity(Intent(this, QRScannerActivity::class.java))
        }

        btnPayment.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        btnReserveSeat.setOnClickListener {
            startActivity(Intent(this, SeatReservationActivity::class.java))
        }
        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 🔥 Get and store FCM token
        storeFCMToken()
    }

    private fun storeFCMToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("FCM", "Generated Token: $token")

            // Replace with dynamically retrieved NIC (from shared preferences or user input)
            val nic = "199012345678" // Temporary placeholder

            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").whereEqualTo("nic", nic)

            userRef.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.documents[0].id
                    db.collection("users").document(docId).update("fcmToken", token)
                        .addOnSuccessListener { Log.d("FCM", "FCM token updated successfully") }
                        .addOnFailureListener { Log.e("FCM", "Failed to update FCM token", it) }
                } else {
                    Log.e("FCM", "User with NIC $nic not found in Firestore")
                }
            }
        }.addOnFailureListener {
            Log.e("FCM", "Failed to get FCM token", it)
        }
    }
}
