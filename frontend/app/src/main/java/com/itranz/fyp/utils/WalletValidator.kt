package com.itranz.fyp.utils

import android.content.Context
import android.widget.Toast
import com.itranz.fyp.network.ApiService
import com.itranz.fyp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object WalletValidator {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    fun checkWalletBalance(context: Context, nic: String, onResult: (Boolean) -> Unit) {
        apiService.getWalletBalance(nic).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val balance = response.body()?.get("balance").toString().toDouble()
                    if (balance >= 50.0) {
                        onResult(true)
                    } else {
                        Toast.makeText(context, "Insufficient balance (Min Rs.50 required).", Toast.LENGTH_LONG).show()
                        onResult(false)
                    }
                } else {
                    Toast.makeText(context, "Failed to get wallet balance.", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        })
    }
}
