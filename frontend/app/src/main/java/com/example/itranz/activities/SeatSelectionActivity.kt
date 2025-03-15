package com.example.itranz.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SeatSelectionActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var availableSeats = 0
    private var busNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)

        db = FirebaseFirestore.getInstance()
        busNumber = intent.getStringExtra("busNumber") ?: ""

        val tvAvailableSeats = findViewById<TextView>(R.id.tvAvailableSeats)
        val btnProceedPayment = findViewById<Button>(R.id.btnProceedPayment)

        db.collection("buses").document(busNumber).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    availableSeats = document.getLong("availableSeats")?.toInt() ?: 0
                    tvAvailableSeats.text = "Available Seats: $availableSeats"
                } else {
                    Toast.makeText(this, "Bus not found!", Toast.LENGTH_SHORT).show()
                }
            }

        btnProceedPayment.setOnClickListener {
            if (availableSeats > 0) {
                val intent = Intent(this, PaymentProcessingActivity::class.java)
                intent.putExtra("busNumber", busNumber)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No seats available!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
