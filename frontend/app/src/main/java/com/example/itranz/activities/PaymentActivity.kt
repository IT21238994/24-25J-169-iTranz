package com.itranz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        db = FirebaseFirestore.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnPay = findViewById<Button>(R.id.btnPay)

        btnPay.setOnClickListener {
            val email = etEmail.text.toString()
            val amount = etAmount.text.toString().toDouble()

            val userRef = db.collection("users").document(email)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val balance = document.getDouble("balance") ?: 0.0
                    if (balance < amount) {
                        Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show()
                    } else {
                        val newBalance = balance - amount
                        userRef.update("balance", newBalance)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
