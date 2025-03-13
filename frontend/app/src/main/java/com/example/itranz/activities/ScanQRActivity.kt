package com.itranz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.google.firebase.firestore.FirebaseFirestore

class ScanQRActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)

        db = FirebaseFirestore.getInstance()
        val scannerView = findViewById<CodeScannerView>(R.id.scannerView)
        val btnProceed = findViewById<Button>(R.id.btnProceed)

        codeScanner = CodeScanner(this, scannerView)
        codeScanner.decodeCallback = { result ->
            runOnUiThread {
                Toast.makeText(this, "Scanned: ${result.text}", Toast.LENGTH_SHORT).show()
                processPayment(result.text)
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        btnProceed.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun processPayment(qrData: String) {
        val parts = qrData.split(",")
        if (parts.size < 3) return

        val nic = parts[0]
        val startStop = parts[1].toInt()
        val endStop = parts[2].toInt()
        val fare = (endStop - startStop) * 5.0  // Example rate Rs. 5 per stop

        db.collection("users").whereEqualTo("nic", nic).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.documents[0]
                    val balance = user.getDouble("balance") ?: 0.0

                    if (balance < fare) {
                        Toast.makeText(this, "Insufficient balance!", Toast.LENGTH_SHORT).show()
                    } else {
                        val newBalance = balance - fare
                        db.collection("users").document(user.id).update("balance", newBalance)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Payment successful! Rs. $fare deducted", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "NIC not found!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
