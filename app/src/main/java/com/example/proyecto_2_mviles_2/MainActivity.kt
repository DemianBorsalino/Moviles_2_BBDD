package com.example.proyecto_2_mviles_2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_2_mviles_2.adapter.ClimaAdapter
import com.example.proyecto_2_mviles_2.databinding.ActivityMainBinding
import com.example.proyecto_2_mviles_2.model.Clima
import com.example.proyecto_2_mviles_2.viewmodel.ClimaViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: ClimaViewModel by viewModels()
    private lateinit var adapter: ClimaAdapter
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        adapter = ClimaAdapter(listOf()) { clima -> openDetail(clima) }
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        vm.climas.observe(this) { list ->
            adapter.update(list)
        }
        vm.loading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        }
        vm.error.observe(this) { err ->
            err?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }

        binding.btnBuscar.setOnClickListener {
            val city = binding.etCity.text.toString().trim()
            val key = prefs.getString("API_KEY", null)
            if (city.isBlank()) {
                Toast.makeText(this, "Ingresa ciudad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (key.isNullOrBlank()) {
                Toast.makeText(this, "Configura API Key en Ajustes", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, SettingsActivity::class.java))
                return@setOnClickListener
            }
            vm.buscarClima(city, key)
        }

        binding.fabRefresh.setOnClickListener { vm.loadSavedClimas() }

        vm.loadSavedClimas()
    }

    private fun openDetail(clima: Clima) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("city", clima.cityName)
            putExtra("temp", clima.temperature)
            putExtra("desc", clima.description)
            putExtra("ts", clima.timestamp)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
