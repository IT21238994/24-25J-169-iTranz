package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.itranz.fyp.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SeatReservationActivity : AppCompatActivity() {

    private lateinit var busNumberInput: EditText
    private lateinit var passengerNicInput: EditText
    private lateinit var reserveSeatButton: Button
    private lateinit var database: DatabaseReference
    private val backendUrl = "http://YOUR-SERVER-IP:3000" // Replace with actual backend URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_reservation)

        busNumberInput = findViewById(R.id.busNumberInput)
        passengerNicInput = findViewById(R.id.passengerNicInput)
        reserveSeatButton = findViewById(R.id.reserveSeatButton)

        database = FirebaseDatabase.getInstance().reference

        reserveSeatButton.setOnClickListener {
            val busNumber = busNumberInput.text.toString().trim()
            val passengerNIC = passengerNicInput.text.toString().trim()

            if (busNumber.isEmpty() || passengerNIC.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            reserveSeatButton.isEnabled = false
            checkSeatAvailabilityAndReserve(busNumber, passengerNIC)
        }
    }

    private fun checkSeatAvailabilityAndReserve(busNumber: String, passengerNIC: String) {
        val seatRef = database.child("buses").child(busNumber).child("availableSeats")

        seatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val availableSeats = snapshot.getValue(Int::class.java) ?: 0
                if (availableSeats > 0) {
                    // Proceed to reserve
                    seatRef.setValue(availableSeats - 1).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this@SeatReservationActivity, "Seat Reserved!", Toast.LENGTH_SHORT).show()
                            sendReservationToBackend(busNumber, passengerNIC)
                        } else {
                            Toast.makeText(this@SeatReservationActivity, "Reservation failed", Toast.LENGTH_SHORT).show()
                            reserveSeatButton.isEnabled = true
                        }
                    }
                } else {
                    Toast.makeText(this@SeatReservationActivity, "No seats available", Toast.LENGTH_SHORT).show()
                    reserveSeatButton.isEnabled = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SeatReservationActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                reserveSeatButton.isEnabled = true
            }
        })
    }

    private fun sendReservationToBackend(busNumber: String, nic: String) {
        val client = OkHttpClient()
        val json = JSONObject(mapOf("busNumber" to busNumber, "nic" to nic))
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val requestApi = Request.Builder()
            .url("$backendUrl/reserveSeat")
            .post(body)
            .build()

        client.newCall(requestApi).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SeatReservationActivity, "Backend Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    reserveSeatButton.isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val message = response.body()?.string()?.let {
                    JSONObject(it).optString("message", "Reservation Complete!")
                }
                runOnUiThread {
                    Toast.makeText(this@SeatReservationActivity, message, Toast.LENGTH_SHORT).show()
                    reserveSeatButton.isEnabled = true
                }
            }
        })
    }
}
