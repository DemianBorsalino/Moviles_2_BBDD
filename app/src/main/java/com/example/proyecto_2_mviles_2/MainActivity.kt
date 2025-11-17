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
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: ClimaViewModel by viewModels()
    private lateinit var adapter: ClimaAdapter
    private lateinit var prefs: SharedPreferences
    private var lastUnits: String = "metric"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Clima"

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        lastUnits = prefs.getString("pref_units", "metric") ?: "metric"

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

        /*binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }*/

        binding.btnClear.setOnClickListener {
            vm.clearAll()
        }

        binding.btnSelect.setOnClickListener {
            adapter.isSelectionMode = true
            adapter.notifyDataSetChanged()

            binding.btnDeleteSelected.visibility = View.VISIBLE
        }

        binding.btnDeleteSelected.setOnClickListener {
            val toDelete = adapter.selectedItems.toList()
            vm.deleteMultiple(toDelete)

            adapter.isSelectionMode = false
            adapter.selectedItems.clear()
            binding.btnDeleteSelected.visibility = View.GONE
        }



        binding.btnBuscar.setOnClickListener {
            var city = binding.etCity.text.toString().trim()

            if (city.isBlank()) {
                city = prefs.getString("pref_default_city", "Buenos Aires") ?: "Buenos Aires"
                Toast.makeText(this, "Usando ciudad por defecto: $city", Toast.LENGTH_SHORT).show()
            }

            val key = prefs.getString("API_KEY", null)
            val units = prefs.getString("pref_units", "metric") ?: "metric"
            /*if (city.isBlank()) {
                Toast.makeText(this, "Ingresa ciudad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }*/
            if (key.isNullOrBlank()) {
                Toast.makeText(this, "Configura API Key en Ajustes", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, SettingsActivity::class.java))
                return@setOnClickListener
            }

            vm.buscarClima(city, key, units)
        }

        /*binding.fabRefresh.setOnClickListener { vm.loadSavedClimas() }

        vm.loadSavedClimas()*/ //Lo mismo que en viewmodel, No tiene sentido el boton de refresh porque lo hace solo
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

    override fun onResume() {
        super.onResume()

        val currentUnits = prefs.getString("pref_units", "metric") ?: "metric"

        if (currentUnits != lastUnits) {
            lastUnits = currentUnits

            // Solo avisar al usuario (opcional)
            Toast.makeText(
                this,
                "Mostrando en ${if (currentUnits == "metric") "Celsius" else "Fahrenheit"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
