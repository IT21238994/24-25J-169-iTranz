package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.itranz.fyp.R

class SeatStatusActivity : AppCompatActivity() {

    private lateinit var busNumberInput: EditText
    private lateinit var checkStatusButton: Button
    private lateinit var seatStatusText: TextView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_status)

        busNumberInput = findViewById(R.id.busNumberInput)
        checkStatusButton = findViewById(R.id.checkStatusButton)
        seatStatusText = findViewById(R.id.seatStatusText)

        dbRef = FirebaseDatabase.getInstance().reference

        checkStatusButton.setOnClickListener {
            val busNumber = busNumberInput.text.toString()
            if (busNumber.isEmpty()) {
                Toast.makeText(this, "Enter bus number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            listenToSeatStatus(busNumber)
        }
    }

    private fun listenToSeatStatus(busNumber: String) {
        val busRef = dbRef.child("buses").child(busNumber)
        busRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val currentSeats = snapshot.child("currentSeats").getValue(Int::class.java) ?: 0
                    val totalSeats = snapshot.child("totalSeats").getValue(Int::class.java) ?: 0
                    seatStatusText.text = "Seats: $currentSeats / $totalSeats"
                } else {
                    seatStatusText.text = "Bus not found."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                seatStatusText.text = "Error: ${error.message}"
            }
        })
    }
}
