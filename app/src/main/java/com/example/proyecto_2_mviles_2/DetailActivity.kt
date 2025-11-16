package com.example.proyecto_2_mviles_2


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val city = intent.getStringExtra("city")
        val temp = intent.getDoubleExtra("temp", 0.0)
        val desc = intent.getStringExtra("desc")
        val ts = intent.getLongExtra("ts", 0L)

        findViewById<TextView>(R.id.tvCityDetail).text = city
        findViewById<TextView>(R.id.tvTempDetail).text = "$temp °C"
        findViewById<TextView>(R.id.tvDescDetail).text = desc

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        findViewById<TextView>(R.id.tvTsDetail).text = sdf.format(Date(ts * 1000))

    }
}