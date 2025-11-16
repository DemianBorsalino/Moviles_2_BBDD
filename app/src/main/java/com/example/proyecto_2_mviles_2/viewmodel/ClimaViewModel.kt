package com.example.proyecto_2_mviles_2.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.proyecto_2_mviles_2.model.Clima
import com.example.proyecto_2_mviles_2.repository.ClimaRepositorySingleton
import kotlinx.coroutines.launch

class ClimaViewModel(application: Application) : AndroidViewModel(application) {

    val climas = ClimaRepositorySingleton.allClimas

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        ClimaRepositorySingleton.initialize(application)
    }

    /*fun loadSavedClimas() {
        // si queremeos hacer que los recargue, pero ya lo hace cada funcion por su lado
    }*/

    fun buscarClima(city: String, apiKey: String, units: String) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = ClimaRepositorySingleton.fetchAndInsert(city, apiKey, units)
            _loading.value = false

            result.exceptionOrNull()?.let {
                _error.value = it.localizedMessage
            }
        }
    }

    fun deleteMultiple(list: List<Clima>) =
        viewModelScope.launch {
            val ids = list.mapNotNull { it.id }
            ClimaRepositorySingleton.deleteMultiple(ids)
        }

    fun clearAll() =
        viewModelScope.launch {
            ClimaRepositorySingleton.deleteAll()
        }
}
