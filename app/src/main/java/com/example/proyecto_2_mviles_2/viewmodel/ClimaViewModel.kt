package com.example.proyecto_2_mviles_2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.proyecto_2_mviles_2.model.Clima
import com.example.proyecto_2_mviles_2.service.ClimaRepositorio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClimaViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ClimaRepositorio.getInstance(application)

    private val _climas = MutableLiveData<List<Clima>>()
    val climas: LiveData<List<Clima>> = _climas

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadSavedClimas() {
        _climas.value = repo.getSavedClimas()
    }

    fun buscarClima(city: String, apiKey: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.fetchClimaFromApi(city, apiKey)
            }
            _loading.value = false
            if (result.isSuccess) {
                // refrescar guardados
                loadSavedClimas()
            } else {
                _error.value = result.exceptionOrNull()?.localizedMessage ?: "Error"
            }
        }
    }
}