package com.itranz.fyp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/create-payment-intent")
    fun createPaymentIntent(@Body body: Map<String, Int>): Call<Map<String, String>>

    @POST("/request-payment")
    fun requestPayment(@Body body: Map<String, String>): Call<Map<String, String>>

    @POST("/reserve-seat")
    fun reserveSeat(@Body body: Map<String, String>): Call<Map<String, String>>
}
