package com.example.itranz.activities  // Ensure this matches your folder structure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.example.itranz.R  // Make sure this matches your R file import

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth  // Declare FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Get references to buttons
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnScanQR = findViewById<Button>(R.id.btnScanQR)
        val btnPayment = findViewById<Button>(R.id.btnPayment)
        val btnReserveSeat = findViewById<Button>(R.id.btnReserveSeat)

        // Set click listeners
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

        // Get and store Firebase Cloud Messaging token
        storeFCMToken()
    }

    private fun storeFCMToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("FCM", "Generated Token: $token")

            // Temporary NIC placeholder, replace it with dynamic retrieval (e.g., from SharedPreferences or user input)
            val nic = "199012345678"

            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").whereEqualTo("nic", nic)

            userRef.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.documents[0].id
                    db.collection("users").document(docId).update("fcmToken", token)
                        .addOnSuccessListener {
                            Log.d("FCM", "FCM token updated successfully")
                        }
                        .addOnFailureListener {
                            Log.e("FCM", "Failed to update FCM token", it)
                        }
                } else {
                    Log.e("FCM", "User with NIC $nic not found in Firestore")
                }
            }
        }.addOnFailureListener {
            Log.e("FCM", "Failed to get FCM token", it)
        }
    }
}
