package com.example.proyecto_2_mviles_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_2_mviles_2.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        val city = intent.getStringExtra("city")
        val temp = intent.getDoubleExtra("temp", 0.0)
        val desc = intent.getStringExtra("desc")
        val ts = intent.getLongExtra("ts", 0L)

        // Setear la información
        binding.tvCityDetail.text = city
        binding.tvTempDetail.text = "$temp °C"
        binding.tvDescDetail.text = desc

        // CORRECCIÓN: Mostrar hora actual en lugar del timestamp del clima
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentTime = System.currentTimeMillis()
        val formattedDate = sdf.format(Date(currentTime))
        binding.tvTsDetail.text = "$formattedDate (UTC+3)"

        // Botón para volver
        binding.btnVolverInicio.setOnClickListener {
            finish()
        }
    }
}