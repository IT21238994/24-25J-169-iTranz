package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SeatReservationActivity : AppCompatActivity() {

    private lateinit var busNumberInput: EditText
    private lateinit var passengerNicInput: EditText
    private lateinit var reserveSeatButton: Button
    private val backendUrl = "http://YOUR-SERVER-IP:3000" // Replace with your actual server URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_reservation)

        // Initializing the UI components
        busNumberInput = findViewById(R.id.busNumberInput)
        passengerNicInput = findViewById(R.id.passengerNicInput)
        reserveSeatButton = findViewById(R.id.reserveSeatButton)

        // Setting click listener for the reserve seat button
        reserveSeatButton.setOnClickListener {
            val busNumber = busNumberInput.text.toString()
            val passengerNIC = passengerNicInput.text.toString()

            // Check if the input fields are empty
            if (busNumber.isEmpty() || passengerNIC.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable the button to prevent multiple clicks
            reserveSeatButton.isEnabled = false

            // Creating request map
            val request = mapOf(
                "busNumber" to busNumber,
                "nic" to passengerNIC
            )

            // Call the function to reserve the seat
            reserveSeat(request)
        }
    }

    private fun reserveSeat(request: Map<String, String>) {
        val client = OkHttpClient()
        val json = JSONObject(request)

        // Setting up the request body
        val body = RequestBody.create(MediaType.get("application/json"), json.toString())

        // Building the API request
        val requestApi = Request.Builder()
            .url("$backendUrl/reserveSeat")
            .post(body)
            .build()

        // Sending the request asynchronously
        client.newCall(requestApi).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    // Show error message if request fails
                    Toast.makeText(this@SeatReservationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    reserveSeatButton.isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    // Parse the response and show the message
                    val message = response.body()?.string()?.let { JSONObject(it).getString("message") }
                        ?: "Reservation Complete!"
                    Toast.makeText(this@SeatReservationActivity, message, Toast.LENGTH_SHORT).show()
                    reserveSeatButton.isEnabled = true
                }
            }
        })
    }
}
