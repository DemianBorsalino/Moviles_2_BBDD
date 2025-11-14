package com.example.proyecto_2_mviles_2.service

import android.content.Context
import com.example.proyecto_2_mviles_2.database.ClimaDbHelper
import com.example.proyecto_2_mviles_2.model.Clima
import com.example.proyecto_2_mviles_2.service.RetrofitClient

class ClimaRepositorio private constructor(private val context: Context) {

    private val dbHelper = ClimaDbHelper(context)

    companion object {
        @Volatile private var INSTANCE: ClimaRepositorio? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ClimaRepositorio(context.applicationContext).also { INSTANCE = it }
            }
    }

    suspend fun fetchClimaFromApi(city: String, apiKey: String,  units: String): Result<Clima> {
        return try {
            val resp = RetrofitClient.apiClient.getCurrentByCity(city, apiKey, units)
            val item = resp.data.firstOrNull()
            if (item != null && item.cityName != null && item.temp != null) {
                val clima = Clima(
                    cityName = item.cityName,
                    description = item.weather?.description ?: "N/A",
                    temperature = item.temp
                )
                // Guardar en SQLite
                dbHelper.insert(clima)
                Result.success(clima)
            } else {
                Result.failure(Exception("No data from API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSavedClimas(): List<Clima> = dbHelper.getAll()
}