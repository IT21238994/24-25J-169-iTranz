package com.itranz

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        db = FirebaseFirestore.getInstance()
        val listView = findViewById<ListView>(R.id.listPayments)

        db.collection("payments").get().addOnSuccessListener { documents ->
            val paymentList = mutableListOf<String>()
            for (doc in documents) {
                val paymentInfo = "ID: ${doc.id}\nFare: Rs. ${doc.getDouble("fare")}\nStatus: ${doc.getString("status")}"
                paymentList.add(paymentInfo)
            }
            listView.adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, paymentList)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch payments", Toast.LENGTH_SHORT).show()
        }
    }
}
