package com.example.itranz.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BestRouteRetrofit {

    // If your server is local, running on port 8000:
    // For the Emulator, "10.0.2.2" is the special alias to localhost.
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: BestRouteApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BestRouteApi::class.java)
    }
}
