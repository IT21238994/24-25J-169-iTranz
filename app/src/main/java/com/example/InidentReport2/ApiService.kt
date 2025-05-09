package com.example.inidentreport2 // Same package as MainActivity.kt

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Define request model
data class ItemRequest(
    val description: String,
    val item_type: String
)

// Define response model
data class MatchResponse(
    val message: String?,
    val found_item_description: String?,
    val lost_item_description : String?,
    val other_details: String?
)

interface ApiService {
    @POST("/match-item/")
    fun matchItem(@Body request: ItemRequest): Call<MatchResponse>
}
