package com.example.proyecto_2_mviles_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.proyecto_2_mviles_2.viewmodel.ClimaViewModel

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        // Configurar toolbar como ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.settingsToolbar)
        setSupportActionBar(toolbar)

        // Activar flecha de volver
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ajustes"

        // Cargar fragment SOLO la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var viewModel: ClimaViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Obtener ViewModel asociado a la Activity
        viewModel = ViewModelProvider(requireActivity())[ClimaViewModel::class.java]

        // Buscar la preferencia que define las unidades
        val unidadPref = findPreference<ListPreference>("pref_units")

        unidadPref?.setOnPreferenceChangeListener { _, newValue ->

            val units = newValue.toString()  // "metric" o "imperial"

            // Llamar conversión
            viewModel.convertirTemperaturas(units)

            true // aceptar el cambio
        }
    }
}