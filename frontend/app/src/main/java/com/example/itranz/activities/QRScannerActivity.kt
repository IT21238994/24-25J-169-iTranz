package com.example.itranz.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.google.zxing.Result

class QRScannerActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)

        scannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)

        // Set up the callback for QR code scanning result
        codeScanner.decodeCallback = { result: Result ->
            runOnUiThread {
                // Display a toast message with the QR code content
                Toast.makeText(this, "Scanned: ${result.text}", Toast.LENGTH_SHORT).show()
                processPayment(result.text) // Handle the scanned data
            }
        }

        // Set up a callback for handling the error (optional)
        codeScanner.errorCallback = { error ->
            runOnUiThread {
                Toast.makeText(this, "Camera error: $error", Toast.LENGTH_LONG).show()
            }
        }

        // Set up onClickListener to trigger scan on tap (optional)
        scannerView.setOnClickListener {
            codeScanner.startPreview() // Restart the scanner preview
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview() // Start scanning when activity resumes
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources() // Stop scanning when activity pauses
    }

    // Handle the scanned QR data
    private fun processPayment(qrData: String) {
        val parts = qrData.split(",")
        if (parts.size < 3) return

        val nic = parts[0]
        val startStop = parts[1].toInt()
        val endStop = parts[2].toInt()
        val fare = (endStop - startStop) * 5.0  // Example rate Rs. 5 per stop

        // You can add Firebase logic or other payment processing here
        Toast.makeText(this, "Processing Payment for NIC: $nic with fare Rs. $fare", Toast.LENGTH_SHORT).show()
    }
}
