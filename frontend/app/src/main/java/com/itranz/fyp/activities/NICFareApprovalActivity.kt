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

class NICFareApprovalActivity : AppCompatActivity() {

    private lateinit var passengerNicInput: EditText
    private lateinit var driverNicInput: EditText
    private lateinit var startStopInput: EditText
    private lateinit var endStopInput: EditText
    private lateinit var sendRequestButton: Button
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nic_fare_approval)

        passengerNicInput = findViewById(R.id.passengerNicInput)
        driverNicInput = findViewById(R.id.driverNicInput)
        startStopInput = findViewById(R.id.startStopInput)
        endStopInput = findViewById(R.id.endStopInput)
        sendRequestButton = findViewById(R.id.sendRequestButton)

        sendRequestButton.setOnClickListener {
            val request = mapOf(
                "passengerNIC" to passengerNicInput.text.toString(),
                "driverNIC" to driverNicInput.text.toString(),
                "startStop" to startStopInput.text.toString(),
                "endStop" to endStopInput.text.toString()
            )

            apiService.requestPayment(request).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    Toast.makeText(this@NICFareApprovalActivity, "Request Sent!", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(this@NICFareApprovalActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
