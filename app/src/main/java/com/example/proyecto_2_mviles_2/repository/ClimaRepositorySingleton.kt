package com.example.proyecto_2_mviles_2.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyecto_2_mviles_2.database.ClimaDbHelper
import com.example.proyecto_2_mviles_2.model.Clima
import com.example.proyecto_2_mviles_2.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

        }catch (e: retrofit2.HttpException) {
            val msg = when (e.code()) {
                400 -> "Solicitud incorrecta (400)."
                401 -> "API Key incorrecta o faltante (401)."
                404 -> "Ciudad no encontrada (404)."
                500 -> "Error del servidor (500)."
                else -> "Error HTTP inesperado: ${e.code()}"
            }
            Result.failure(Exception(msg))
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Sin conexión a Internet"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado"))
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

    suspend fun convertAllTemperatures(toUnits: String) = withContext(Dispatchers.IO) {
        val db = dbHelper ?: return@withContext

        val all = db.getAllClimas()

        val converted = all.map { clima ->
            val newTemp = if (toUnits == "metric") {
                // Fahrenheit → Celsius
                (clima.temperature - 32.0) * 5.0 / 9.0
            } else {
                // Celsius → Fahrenheit
                (clima.temperature * 9.0 / 5.0) + 32.0
            }

            clima.copy(
                id = clima.id,
                temperature = newTemp
            )
        }

        db.updateMultipleClimas(converted)

        withContext(Dispatchers.Main) {
            loadClimas()
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
