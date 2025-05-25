package com.example.itranz.network

import com.example.itranz.model.BestRouteRequest
import com.example.itranz.model.BestRouteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BestRouteApi {

    // If your endpoint is GET (no request body):
    //@GET("bestroute")
    //suspend fun getBestRoute(): BestRouteResponse

    // If your endpoint is POST, use:
    @POST("bestroute")
    suspend fun getBestRoute(@Body request: BestRouteRequest): BestRouteResponse
}
