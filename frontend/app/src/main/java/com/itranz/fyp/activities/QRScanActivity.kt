package com.itranz.fyp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.IntentIntegrator

class QRScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan Driver QR Code")
        integrator.setOrientationLocked(false)
        integrator.captureActivity = CaptureActivity::class.java
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // Example QR Content: "routeName:Panadura-Kandy,busName:Bus XYZ"
                val qrData = result.contents.split(",")
                val route = qrData[0].split(":")[1]
                val bus = qrData[1].split(":")[1]

                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("routeName", route)
                intent.putExtra("busName", bus)
                startActivity(intent)
            } else {
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}