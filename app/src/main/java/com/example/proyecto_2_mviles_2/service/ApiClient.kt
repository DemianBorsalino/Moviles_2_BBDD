package com.example.proyecto_2_mviles_2.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherbitResponseItem(
    @SerializedName("city_name") val cityName: String?,
    @SerializedName("temp") val temp: Double?,
    val weather: WeatherDesc?
)

data class WeatherDesc(val description: String?)

data class WeatherbitResponse(val data: List<WeatherbitResponseItem>)

interface ApiClient {
    // ejemplo: https://api.weatherbit.io/v2.0/current?city=London&key=API_KEY
    @GET("v2.0/current")
    suspend fun getCurrentByCity(
        @Query("city") city: String,
        @Query("key") apiKey: String,
        @Query("units") units: String = "M"
    ): WeatherbitResponse
}
