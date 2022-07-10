package com.gourav.currencyconverter.data.network

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("api/latest.json")
    suspend fun getRates(
        @Query("app_id") app_id: String,
        @Query("base") base: String
    ): Response<JsonElement>
}