package com.example.proyecto_2_mviles_2.model


    data class Clima(
        val id: Int = 0,
        val cityName: String,
        val description: String,
        val temperature: Double,
        val timestamp: Long = System.currentTimeMillis()
    )