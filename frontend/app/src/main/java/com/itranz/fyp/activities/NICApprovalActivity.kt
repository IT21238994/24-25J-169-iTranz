package com.itranz.fyp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.itranz.fyp.R

class NICApprovalActivity : AppCompatActivity() {

    private val backendUrl = "http://10.0.2.2:3000" // Emulator IP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nic_approval)

        val nicTextView = findViewById<TextView>(R.id.nicTextView)
        val amountTextView = findViewById<TextView>(R.id.amountTextView)
        val approveButton = findViewById<Button>(R.id.approveButton)
        val rejectButton = findViewById<Button>(R.id.rejectButton)

        val nic = intent.getStringExtra("nic") ?: ""
        val amount = intent.getStringExtra("amount") ?: ""

        nicTextView.text = "NIC: $nic"
        amountTextView.text = "Amount: Rs. $amount"

        approveButton.setOnClickListener {
            updatePaymentStatus(nic, "approved")
        }

        rejectButton.setOnClickListener {
            updatePaymentStatus(nic, "rejected")
        }
    }

    private fun updatePaymentStatus(nic: String, status: String) {
        val client = OkHttpClient()
        val json = JSONObject()
        json.put("nic", nic)
        json.put("status", status)

        val body = RequestBody.create(MediaType.get("application/json"), json.toString())
        val request = Request.Builder()
            .url("$backendUrl/update-payment-status")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Status updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        })
    }
}
