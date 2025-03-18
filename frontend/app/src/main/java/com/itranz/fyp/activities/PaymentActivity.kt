package com.itranz.fyp.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import com.stripe.android.PaymentConfiguration
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentLauncherFactory
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.model.ConfirmPaymentIntentParams
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PaymentActivity : AppCompatActivity() {
    private lateinit var paymentLauncher: PaymentLauncher
    private val stripePublishableKey = "pk_test_51QR81201Cm8uyizaiJfQ2hpnq8PWp3AQghp8NsUTUyi0MITxpRc4Jp6UX9K9idVfqvGja3dcdQN3C7Cv22fKkK3O00vBBPbK9F"
    private val backendUrl = "http://YOUR-SERVER-IP:3000" // Replace with your actual server URL

    private lateinit var payButton: Button
    private lateinit var fareTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var fareAmountLKR: Int = 0
    private val userNIC = "123456789V" // Replace with dynamic NIC from logged-in user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Initialize Stripe
        PaymentConfiguration.init(applicationContext, stripePublishableKey)
        setupPaymentLauncher()

        // UI Components
        payButton = findViewById(R.id.payButton)
        fareTextView = findViewById(R.id.tvFare)
        progressBar = findViewById(R.id.progressBar)

        payButton.setOnClickListener {
            if (fareAmountLKR > 0) {
                validateWalletAndPay(fareAmountLKR)
            } else {
                Toast.makeText(this, "Calculate fare first", Toast.LENGTH_SHORT).show()
            }
        }

        val btnCalculateFare: Button = findViewById(R.id.btnCalculateFare)
        btnCalculateFare.setOnClickListener {
            // Hardcoded example, replace with dynamic logic
            fareAmountLKR = 580
            fareTextView.text = "Fare: Rs. $fareAmountLKR"
        }
    }

    private fun setupPaymentLauncher() {
        paymentLauncher = PaymentLauncherFactory.create(
            context = this,
            publishableKey = stripePublishableKey,
            stripeAccountId = null,
            callback = { result: PaymentResult ->
                when (result) {
                    is PaymentResult.Completed -> {
                        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                        updatePaymentStatus("completed")
                    }
                    is PaymentResult.Canceled -> {
                        Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                        updatePaymentStatus("cancelled")
                    }
                    is PaymentResult.Failed -> {
                        Toast.makeText(this, "Payment Failed: ${result.throwable.message}", Toast.LENGTH_LONG).show()
                        updatePaymentStatus("failed")
                    }
                }
                progressBar.visibility = View.GONE
            }
        )
    }

    private fun calculateFare(busNumber: String, startStop: String, endStop: String) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("busNumber", busNumber)
            put("startStop", startStop)
            put("endStop", endStop)
        }
        val body = RequestBody.create(MediaType.get("application/json"), json.toString())
        val request = Request.Builder()
            .url("$backendUrl/calculateFare")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@PaymentActivity, "Failed to calculate fare", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val resString = response.body()?.string()
                val fare = JSONObject(resString).getInt("fare")
                fareAmountLKR = fare
                runOnUiThread {
                    fareTextView.text = "Fare: Rs. $fare"
                    payButton.isEnabled = true
                }
            }
        })
    }


    private fun validateWalletAndPay(amountLKR: Int) {
        progressBar.visibility = View.VISIBLE
        val client = OkHttpClient()
        val json = JSONObject().apply { put("nic", userNIC); put("amount", amountLKR) }
        val body = RequestBody.create(MediaType.parse("application/json"), json.toString())

        val request = Request.Builder()
            .url("$backendUrl/confirmPayment")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Payment request failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                }

                val responseData = response.body()?.string()
                val jsonObject = JSONObject(responseData ?: "")

                if (response.isSuccessful) {
                    val clientSecret = jsonObject.getString("clientSecret")
                    val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(
                        "pm_card_visa", // Use Stripe SDK to get actual method ID
                        clientSecret
                    )
                    runOnUiThread {
                        progressBar.visibility = View.VISIBLE
                        paymentLauncher.confirm(confirmParams)
                    }
                } else {
                    runOnUiThread {
                        val message = jsonObject.optString("message", "Error")
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun updatePaymentStatus(status: String) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("passengerNIC", userNIC)
            put("status", status)
        }
        val body = RequestBody.create(MediaType.parse("application/json"), json.toString())

        val request = Request.Builder()
            .url("$backendUrl/updatePaymentStatus")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { /* Log error */ }
            override fun onResponse(call: Call, response: Response) { /* Log success */ }
        })
    }
}
