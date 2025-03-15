package com.example.itranz.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PassengerApprovalActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var paymentId: String? = null
    private var senderId: String? = null
    private var amount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_approval)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val tvPaymentInfo = findViewById<TextView>(R.id.tvPaymentInfo)
        val btnApprove = findViewById<Button>(R.id.btnApprove)
        val btnReject = findViewById<Button>(R.id.btnReject)

        db.collection("payments")
            .whereEqualTo("receiverId", auth.currentUser!!.uid)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    paymentId = doc.id
                    senderId = doc.getString("senderId")
                    amount = doc.getDouble("amount") ?: 0.0

                    tvPaymentInfo.text = "Request: Rs. $amount"
                }
            }

        btnApprove.setOnClickListener {
            if (paymentId != null && senderId != null) {
                processPayment()
            }
        }

        btnReject.setOnClickListener {
            if (paymentId != null) {
                db.collection("payments").document(paymentId!!).update("status", "rejected")
                Toast.makeText(this, "Payment Rejected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processPayment() {
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { doc ->
                val balance = doc.getDouble("balance") ?: 0.0

                if (balance >= amount) {
                    val newBalance = balance - amount

                    db.collection("users").document(auth.currentUser!!.uid)
                        .update("balance", newBalance)
                        .addOnSuccessListener {
                            completeTransaction()
                        }
                } else {
                    Toast.makeText(this, "Insufficient Balance!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun completeTransaction() {
        db.collection("payments").document(paymentId!!).update("status", "approved")
        db.collection("users").document(senderId!!).get()
            .addOnSuccessListener { doc ->
                val senderBalance = doc.getDouble("balance") ?: 0.0
                db.collection("users").document(senderId!!)
                    .update("balance", senderBalance + amount)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Payment Approved", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}
