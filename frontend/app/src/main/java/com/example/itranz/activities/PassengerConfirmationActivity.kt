package com.example.itranz.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PassengerConfirmationActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_confirmation)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val tvPaymentDetails = findViewById<TextView>(R.id.tvPaymentDetails)
        val btnApprove = findViewById<Button>(R.id.btnApprove)
        val btnReject = findViewById<Button>(R.id.btnReject)

        val paymentId = intent.getStringExtra("paymentId") ?: return
        val passengerId = auth.currentUser?.uid ?: return

        db.collection("payments").document(paymentId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val fare = document.getDouble("fare") ?: 0.0
                tvPaymentDetails.text = "Payment of Rs. $fare pending. Approve?"
            }
        }

        btnApprove.setOnClickListener {
            updatePaymentStatus(paymentId, "approved")
        }

        btnReject.setOnClickListener {
            updatePaymentStatus(paymentId, "rejected")
        }
    }

    private fun updatePaymentStatus(paymentId: String, status: String) {
        db.collection("payments").document(paymentId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Payment $status!", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
