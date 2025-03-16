package com.itranz.fyp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://YOUR_IP:5000") // Replace with server IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
