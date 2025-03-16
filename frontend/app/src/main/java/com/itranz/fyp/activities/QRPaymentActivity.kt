package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import com.itranz.fyp.network.ApiService
import com.itranz.fyp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QRPaymentActivity : AppCompatActivity() {

    private lateinit var qrScannerButton: Button
    private lateinit var startStopInput: EditText
    private lateinit var endStopInput: EditText
    private lateinit var calculateFareButton: Button
    private lateinit var payButton: Button
    private lateinit var fareTextView: TextView
    private var fareAmountLKR: Int = 0
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_payment)

        qrScannerButton = findViewById(R.id.qrScanButton)
        startStopInput = findViewById(R.id.startStopInput)
        endStopInput = findViewById(R.id.endStopInput)
        calculateFareButton = findViewById(R.id.calculateFareButton)
        payButton = findViewById(R.id.payButton)
        fareTextView = findViewById(R.id.fareTextView)

        qrScannerButton.setOnClickListener {
            // Implement QR Scanner and get bus info
        }

        calculateFareButton.setOnClickListener {
            val startStop = startStopInput.text.toString()
            val endStop = endStopInput.text.toString()
            // Simple fare calculation logic (example)
            fareAmountLKR = 100 // Placeholder
            fareTextView.text = "Fare: Rs. $fareAmountLKR"
        }

        payButton.setOnClickListener {
            initiatePayment(fareAmountLKR)
        }
    }

    private fun initiatePayment(amount: Int) {
        val paymentRequest = mapOf("amountLKR" to amount)
        apiService.createPaymentIntent(paymentRequest).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                val clientSecret = response.body()?.get("clientSecret")
                Toast.makeText(this@QRPaymentActivity, "Payment Intent Created", Toast.LENGTH_SHORT).show()
                // Use clientSecret with Stripe SDK to proceed
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@QRPaymentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
