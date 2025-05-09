package com.example.inidentreport2

//import LostFound
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select)  // Linking to select.xml

        val buttonIncident = findViewById<Button>(R.id.buttonIncidentReport)
        val buttonLostFound = findViewById<Button>(R.id.buttonLostFound)

        // Navigate to Incident Report (activity_main.xml)
        buttonIncident.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Lost & Found (lost_found.xml)
        buttonLostFound.setOnClickListener {
            val intent = Intent(this, LostFound::class.java)
            startActivity(intent)
        }
    }
}
