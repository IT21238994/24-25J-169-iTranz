package com.example.itranz.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class GenerateQRActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr)

        db = FirebaseFirestore.getInstance()

        val etNIC = findViewById<EditText>(R.id.etNIC)
        val etStartStop = findViewById<EditText>(R.id.etStartStop)
        val etEndStop = findViewById<EditText>(R.id.etEndStop)
        val btnGenerateQR = findViewById<Button>(R.id.btnGenerateQR)
        val ivQRCode = findViewById<ImageView>(R.id.ivQRCode)

        btnGenerateQR.setOnClickListener {
            val nic = etNIC.text.toString()
            val startStop = etStartStop.text.toString()
            val endStop = etEndStop.text.toString()

            if (nic.isEmpty() || startStop.isEmpty() || endStop.isEmpty()) {
                return@setOnClickListener
            }

            val qrData = "$nic,$startStop,$endStop"
            val qrCodeBitmap = generateQRCode(qrData)

            if (qrCodeBitmap != null) {
                ivQRCode.setImageBitmap(qrCodeBitmap)
            }
        }
    }

    private fun generateQRCode(data: String): Bitmap? {
        val writer = QRCodeWriter()
        return try {
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 500, 500)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            null
        }
    }
}
