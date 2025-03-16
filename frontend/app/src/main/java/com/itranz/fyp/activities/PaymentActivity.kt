package com.itranz.fyp.activities


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import com.google.firebase.database.*
import com.stripe.android.PaymentConfiguration
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentLauncherFactory
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentIntent
import com.stripe.android.view.PaymentMethodsActivityStarter

class PaymentActivity : AppCompatActivity() {

    private lateinit var tvBusName: TextView
    private lateinit var tvRoute: TextView
    private lateinit var spinnerStartStop: Spinner
    private lateinit var spinnerEndStop: Spinner
    private lateinit var btnCalculateFare: Button
    private lateinit var tvFare: TextView
    private lateinit var btnPay: Button

    private lateinit var database: DatabaseReference
    private var busRoute: String = ""
    private var fare: Int = 0
    private var clientSecret: String = ""

    private lateinit var paymentLauncher: PaymentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        tvBusName = findViewById(R.id.tvBusName)
        tvRoute = findViewById(R.id.tvRoute)
        spinnerStartStop = findViewById(R.id.spinnerStartStop)
        spinnerEndStop = findViewById(R.id.spinnerEndStop)
        btnCalculateFare = findViewById(R.id.btnCalculateFare)
        tvFare = findViewById(R.id.tvFare)
        btnPay = findViewById(R.id.btnPay)

        // Retrieve data from QR
        val busName = intent.getStringExtra("busName")
        busRoute = intent.getStringExtra("busRoute") ?: ""

        tvBusName.text = "Bus Name: $busName"
        tvRoute.text = "Route: $busRoute"

        database = FirebaseDatabase.getInstance().reference

        loadBusStops()

        btnCalculateFare.setOnClickListener {
            calculateFare()
        }

        btnPay.setOnClickListener {
            initiatePayment()
        }

        // Initialize Stripe with publishable key
        PaymentConfiguration.init(applicationContext, "pk_test_51YourKey")

        paymentLauncher = PaymentLauncherFactory.create(this, "pk_test_51YourKey", ::onPaymentResult)
    }

    private fun loadBusStops() {
        val stopsRef = database.child("routes").child(busRoute).child("stops")
        stopsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stopNames = mutableListOf<String>()
                for (stop in snapshot.children) {
                    val stopName = stop.child("name").getValue(String::class.java)
                    stopName?.let { stopNames.add(it) }
                }
                val adapter = ArrayAdapter(this@PaymentActivity, android.R.layout.simple_spinner_item, stopNames)
                spinnerStartStop.adapter = adapter
                spinnerEndStop.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PaymentActivity, "Failed to load stops.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateFare() {
        val startStop = spinnerStartStop.selectedItem.toString()
        val endStop = spinnerEndStop.selectedItem.toString()

        val stopsRef = database.child("routes").child(busRoute).child("stops")

        stopsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var startNumber = 0
                var endNumber = 0

                for (stop in snapshot.children) {
                    val name = stop.child("name").getValue(String::class.java)
                    val number = stop.child("number").getValue(Int::class.java) ?: 0
                    if (name == startStop) startNumber = number
                    if (name == endStop) endNumber = number
                }
                val diff = kotlin.math.abs(endNumber - startNumber)
                fare = diff * 10  // Rs. 10 per stop
                tvFare.text = "Fare: Rs. $fare"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initiatePayment() {
        if (fare <= 0) {
            Toast.makeText(this, "Calculate fare first", Toast.LENGTH_SHORT).show()
            return
        }
        // Simulate clientSecret retrieval (replace with backend API call)
        clientSecret = "pi_3YourClientSecret"  // Replace with real clientSecret

        val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodId(
            paymentMethodId = "pm_card_visa", // Simulate with test method
            clientSecret = clientSecret
        )
        paymentLauncher.confirm(confirmParams)
    }

    private fun onPaymentResult(result: PaymentLauncher.PaymentResult) {
        when (result) {
            is PaymentLauncher.PaymentResult.Completed -> {
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
            }
            is PaymentLauncher.PaymentResult.Canceled -> {
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show()
            }
            is PaymentLauncher.PaymentResult.Failed -> {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}