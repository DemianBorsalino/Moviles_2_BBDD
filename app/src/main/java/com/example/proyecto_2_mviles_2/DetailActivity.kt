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

        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val units = prefs.getString("pref_units", "metric") ?: "metric"
        val unitSymbol = if (units == "imperial") "°F" else "°C"

        val city = intent.getStringExtra("city")
        val temp = intent.getDoubleExtra("temp", 0.0)
        val desc = intent.getStringExtra("desc")
        val ts = intent.getLongExtra("ts", 0L)

        binding.tvCityDetail.text = city
        binding.tvTempDetail.text = "$temp $unitSymbol"
        binding.tvDescDetail.text = desc

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentTime = System.currentTimeMillis()
        val formattedDate = sdf.format(Date(currentTime))
        binding.tvTsDetail.text = "$formattedDate (UTC+3)"

        binding.btnVolverInicio.setOnClickListener {
            finish()
        }
    }
}