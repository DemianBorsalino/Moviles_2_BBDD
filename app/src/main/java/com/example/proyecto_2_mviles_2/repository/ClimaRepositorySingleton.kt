package com.example.proyecto_2_mviles_2.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyecto_2_mviles_2.database.ClimaDbHelper
import com.example.proyecto_2_mviles_2.model.Clima
import com.example.proyecto_2_mviles_2.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ClimaRepositorySingleton {

    private var dbHelper: ClimaDbHelper? = null

    private val _allClimas = MutableLiveData<List<Clima>>()
    val allClimas: LiveData<List<Clima>> = _allClimas


    // --------------------------------------
    //   Inicializar el repositorio
    // --------------------------------------
    fun initialize(context: Context) {
        if (dbHelper == null) {
            dbHelper = ClimaDbHelper(context)
            loadClimas()
        }
    }

    private fun requireDbHelper(): ClimaDbHelper {
        return dbHelper ?: throw IllegalStateException(
            "ClimaRepositorySingleton no ha sido inicializado. Llamá a initialize(context)."
        )
    }

    suspend fun fetchAndInsert(city: String, key: String, units: String): Result<Clima> {
        return try {
            val response = RetrofitClient.apiClient.getCurrentByCity(city, key, units)
            val item = response.data.firstOrNull()
                ?: return Result.failure(Exception("API returned empty"))

            val clima = Clima(
                cityName = item.cityName ?: city,
                description = item.weather?.description ?: "N/A",
                temperature = item.temp ?: 0.0
            )

            withContext(Dispatchers.IO) {
                requireDbHelper().insertClima(clima)
            }

            loadClimas()
            Result.success(clima)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --------------------------------------
    //   Cargar datos desde SQLite
    // --------------------------------------
    private fun loadClimas() {
        _allClimas.value = requireDbHelper().getAllClimas()
    }


    // --------------------------------------
    //   CRUD
    // --------------------------------------
    suspend fun insertClima(clima: Clima) {
        withContext(Dispatchers.IO) {
            requireDbHelper().insertClima(clima)
            withContext(Dispatchers.Main) {
                loadClimas()
            }
        }
    }

    suspend fun deleteClima(id: Int) {
        withContext(Dispatchers.IO) {
            requireDbHelper().deleteClima(id)
            withContext(Dispatchers.Main) {
                loadClimas()
            }
        }
    }

    suspend fun deleteMultiple(ids: List<Int>) {
        withContext(Dispatchers.IO) {
            requireDbHelper().deleteMultipleClimas(ids)
            withContext(Dispatchers.Main) {
                loadClimas()
            }
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            requireDbHelper().deleteAllClimas()
            withContext(Dispatchers.Main) {
                loadClimas()
            }
        }
    }
}
