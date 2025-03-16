package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.network.ApiService
import com.itranz.fyp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.itranz.fyp.R

class SeatReservationActivity : AppCompatActivity() {

    private lateinit var busNumberInput: EditText
    private lateinit var passengerNicInput: EditText
    private lateinit var reserveSeatButton: Button
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_reservation)

        busNumberInput = findViewById(R.id.busNumberInput)
        passengerNicInput = findViewById(R.id.passengerNicInput)
        reserveSeatButton = findViewById(R.id.reserveSeatButton)

        reserveSeatButton.setOnClickListener {
            val request = mapOf(
                "busNumber" to busNumberInput.text.toString(),
                "nic" to passengerNicInput.text.toString()
            )

            apiService.reserveSeat(request).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    Toast.makeText(this@SeatReservationActivity, response.body()?.get("message"), Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(this@SeatReservationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
