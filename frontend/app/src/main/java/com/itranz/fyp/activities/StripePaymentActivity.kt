package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import com.itranz.fyp.network.ApiService
import com.itranz.fyp.network.RetrofitClient
import com.stripe.android.PaymentConfiguration
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentLauncherContract
import com.stripe.android.model.ConfirmPaymentIntentParams
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StripePaymentActivity : AppCompatActivity() {

    private lateinit var amountInput: EditText
    private lateinit var payButton: Button
    private lateinit var paymentLauncher: PaymentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_payment)

        amountInput = findViewById(R.id.amountInput)
        payButton = findViewById(R.id.payButton)

        PaymentConfiguration.init(applicationContext, "YOUR_PUBLISHABLE_KEY")

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        payButton.setOnClickListener {
            val amount = amountInput.text.toString()

            apiService.createPaymentIntent(mapOf("amount" to amount)).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    val clientSecret = response.body()?.get("clientSecret")
                    clientSecret?.let {
                        confirmPayment(it)
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(this@StripePaymentActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        paymentLauncher = PaymentLauncher.Companion.create(
            this,
            PaymentConfiguration.getInstance(applicationContext).publishableKey,
            PaymentLauncherContract.Config("YOUR_PUBLISHABLE_KEY")
        ) { result ->
            when (result) {
                is PaymentLauncher.Result.Completed -> Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
                is PaymentLauncher.Result.Canceled -> Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                is PaymentLauncher.Result.Failed -> Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmPayment(clientSecret: String) {
        val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodId("pm_card_visa", clientSecret)
        paymentLauncher.confirm(confirmParams)
    }
}