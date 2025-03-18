package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.itranz.fyp.R
import com.google.firebase.database.FirebaseDatabase

class FareApprovalActivity : AppCompatActivity() {

    private lateinit var nicInput: EditText
    private lateinit var startSpinner: Spinner
    private lateinit var endSpinner: Spinner
    private lateinit var requestBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fare_approval)

        nicInput = findViewById(R.id.editNIC)
        startSpinner = findViewById(R.id.spinnerStart)
        endSpinner = findViewById(R.id.spinnerEnd)
        requestBtn = findViewById(R.id.btnRequest)

        requestBtn.setOnClickListener {
            val nic = nicInput.text.toString()
            val start = startSpinner.selectedItem.toString()
            val end = endSpinner.selectedItem.toString()
            sendPaymentRequest(nic, start, end)
        }
    }

    private fun sendPaymentRequest(nic: String, start: String, end: String) {
        val ref = FirebaseDatabase.getInstance().getReference("paymentRequests")
        val requestId = ref.push().key!!
        val requestData = mapOf(
            "nic" to nic,
            "start" to start,
            "end" to end,
            "status" to "pending"
        )
        ref.child(requestId).setValue(requestData).addOnSuccessListener {
            Toast.makeText(this, "Request Sent!", Toast.LENGTH_SHORT).show()
        }
    }
}
