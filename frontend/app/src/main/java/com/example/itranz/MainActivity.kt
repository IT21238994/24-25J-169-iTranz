package com.example.itranz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnFindBusRoute: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnFindBusRoute = findViewById(R.id.btnFindBusRoute)

        // Set click listener for the Find Bus Route button
        btnFindBusRoute.setOnClickListener {
            // Create an intent to navigate to BusRouteActivity
            val intent = Intent(this, BusRouteActivity::class.java)
            startActivity(intent)
        }
    }
}