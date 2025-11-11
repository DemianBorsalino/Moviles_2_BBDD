package com.example.proyecto_2_mviles_2.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


data class Clima(
    val cityName: String,
    val description: String,
    val temperature: Double,
    val timestamp: Long = System.currentTimeMillis()
)