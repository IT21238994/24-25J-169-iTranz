package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import com.stripe.android.PaymentConfiguration
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentLauncherResult
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class TopUpWalletActivity : AppCompatActivity() {

    private lateinit var amountInput: EditText
    private lateinit var topUpButton: Button
    private lateinit var paymentLauncher: PaymentLauncher
    private val backendUrl = "http://YOUR-SERVER-IP:3000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topup_wallet)

        amountInput = findViewById(R.id.amountInput)
        topUpButton = findViewById(R.id.topUpButton)

        PaymentConfiguration.init(applicationContext, "pk_test_YOUR_PUBLIC_KEY")

        paymentLauncher = PaymentLauncher.Companion.create(this, "pk_test_YOUR_PUBLIC_KEY", ::onPaymentResult)

        topUpButton.setOnClickListener {
            val amount = amountInput.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show()
            } else {
                createPaymentIntent(amount.toDouble())
            }
        }
    }

    private fun createPaymentIntent(amount: Double) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("amount", amount)
        }
        val body = RequestBody.create(MediaType.get("application/json"), json.toString())
        val request = Request.Builder()
            .url("$backendUrl/create-topup-intent")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@TopUpWalletActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.string()?.let {
                    val jsonResponse = JSONObject(it)
                    val clientSecret = jsonResponse.getString("clientSecret")
                    runOnUiThread {
                        paymentLauncher.confirm(clientSecret)
                    }
                }
            }
        })
    }

    private fun onPaymentResult(result: PaymentLauncherResult) {
        when (result) {
            is PaymentLauncherResult.Completed -> Toast.makeText(this, "Top-up Successful", Toast.LENGTH_SHORT).show()
            is PaymentLauncherResult.Canceled -> Toast.makeText(this, "Top-up Canceled", Toast.LENGTH_SHORT).show()
            is PaymentLauncherResult.Failed -> Toast.makeText(this, "Top-up Failed: ${result.throwable.message}", Toast.LENGTH_SHORT).show()
        }
    }
}