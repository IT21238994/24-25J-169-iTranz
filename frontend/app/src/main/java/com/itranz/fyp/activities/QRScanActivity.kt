package com.itranz.fyp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import com.stripe.android.PaymentConfiguration
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentLauncherFactory
import com.stripe.android.payments.paymentlauncher.PaymentResult

class QRScanActivity : AppCompatActivity() {

    private lateinit var startStopSpinner: Spinner
    private lateinit var endStopSpinner: Spinner
    private lateinit var calculateBtn: Button
    private lateinit var payBtn: Button
    private lateinit var fareText: TextView
    private lateinit var paymentLauncher: PaymentLauncher

    private var busId: String = ""
    private var fareAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)

        startStopSpinner = findViewById(R.id.spinnerStart)
        endStopSpinner = findViewById(R.id.spinnerEnd)
        calculateBtn = findViewById(R.id.btnCalculate)
        payBtn = findViewById(R.id.btnPay)
        fareText = findViewById(R.id.txtFare)

        // Initialize Stripe Payment
        PaymentConfiguration.init(applicationContext, "pk_test_YourStripePublicKey")
        paymentLauncher = PaymentLauncherFactory.create(
            this,
            "pk_test_YourStripePublicKey",
            ::onPaymentResult
        )

        // QR Scanner Trigger
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan Driver QR")
        integrator.setBeepEnabled(true)
        integrator.initiateScan()

        calculateBtn.setOnClickListener {
            val start = startStopSpinner.selectedItem.toString()
            val end = endStopSpinner.selectedItem.toString()
            calculateFare(start, end)
        }

        payBtn.setOnClickListener {
            initiatePayment(fareAmount)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                try {
                    val qrData = JSONObject(result.contents)
                    val busNumber = qrData.getString("busNumber")
                    val busRoute = qrData.getString("busRoute")
                    val driverId = qrData.getString("driverId")

                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("busNumber", busNumber)
                    intent.putExtra("busRoute", busRoute)
                    intent.putExtra("driverId", driverId)
                    startActivity(intent)
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid QR Code Format", Toast.LENGTH_LONG).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun fetchBusDetails(busId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("busDetails/$busId")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val route = snapshot.child("route").getValue(String::class.java) ?: ""
                loadStopsForRoute(route)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadStopsForRoute(route: String) {
        val stopRef = FirebaseDatabase.getInstance().getReference("routes/$route/stops")
        stopRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stopList = mutableListOf<String>()
                snapshot.children.forEach { stopList.add(it.child("name").value.toString()) }
                val adapter = ArrayAdapter(this@QRScanActivity, android.R.layout.simple_spinner_item, stopList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                startStopSpinner.adapter = adapter
                endStopSpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun calculateFare(start: String, end: String) {
        // Fetch fare difference logic
        val fareRef = FirebaseDatabase.getInstance().getReference("fareCalculation")
        fareRef.child("$start-$end").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fareAmount = snapshot.getValue(Int::class.java) ?: 0
                fareText.text = "Fare: Rs. $fareAmount"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initiatePayment(amount: Int) {
        // Call backend /create-payment-intent to get clientSecret
        // Launch Stripe payment
        val clientSecret = "your_client_secret_from_backend" // Fetch from API
        paymentLauncher.confirm(PaymentLauncher.ConfirmParams.create(clientSecret))
    }

    private fun onPaymentResult(paymentResult: PaymentResult) {
        when (paymentResult) {
            is PaymentResult.Completed -> Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            is PaymentResult.Failed -> Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            is PaymentResult.Canceled -> Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}
