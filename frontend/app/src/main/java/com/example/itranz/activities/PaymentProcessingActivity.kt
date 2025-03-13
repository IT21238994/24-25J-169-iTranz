package com.itranz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PaymentProcessingActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var busNumber = ""
    private val seatPrice = 100.0  // Example price per seat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_processing)

        db = FirebaseFirestore.getInstance()
        busNumber = intent.getStringExtra("busNumber") ?: ""

        val tvTotalFare = findViewById<TextView>(R.id.tvTotalFare)
        val btnConfirmPayment = findViewById<Button>(R.id.btnConfirmPayment)

        val totalFare = seatPrice
        tvTotalFare.text = "Total Fare: Rs. $totalFare"

        btnConfirmPayment.setOnClickListener {
            db.collection("users").document("currentUserNIC").get()
                .addOnSuccessListener { user ->
                    val balance = user.getDouble("balance") ?: 0.0

                    if (balance >= totalFare) {
                        val newBalance = balance - totalFare
                        db.collection("users").document("currentUserNIC").update("balance", newBalance)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
                                updateSeatAvailability()
                            }
                    } else {
                        Toast.makeText(this, "Insufficient Balance!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateSeatAvailability() {
        db.collection("buses").document(busNumber).get()
            .addOnSuccessListener { bus ->
                val currentSeats = bus.getLong("availableSeats")?.toInt() ?: 0
                if (currentSeats > 0) {
                    val newSeats = currentSeats - 1
                    db.collection("buses").document(busNumber).update("availableSeats", newSeats)
                }
            }
    }
}
