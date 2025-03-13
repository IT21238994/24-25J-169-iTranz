package com.itranz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SeatReservationActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_reservation)

        db = FirebaseFirestore.getInstance()

        val etBusNumber = findViewById<EditText>(R.id.etBusNumber)
        val btnCheckSeats = findViewById<Button>(R.id.btnCheckSeats)

        btnCheckSeats.setOnClickListener {
            val busNumber = etBusNumber.text.toString()

            if (busNumber.isEmpty()) {
                Toast.makeText(this, "Enter Bus Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SeatSelectionActivity::class.java)
            intent.putExtra("busNumber", busNumber)
            startActivity(intent)
        }
    }
}
