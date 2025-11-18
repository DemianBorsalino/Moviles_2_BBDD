package com.example.proyecto_2_mviles_2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_2_mviles_2.databinding.ItemClimaBinding
import com.example.proyecto_2_mviles_2.model.Clima
import java.text.SimpleDateFormat
import java.util.*

class ClimaAdapter(
    private var items: List<Clima>,
    private var units: String,
    private val onItemClick: (Clima) -> Unit
) : RecyclerView.Adapter<ClimaAdapter.VH>() {

    var isSelectionMode = false
    val selectedItems = mutableSetOf<Clima>()
    private var timer: Timer? = null
    private var isTimeUpdatesRunning = false

    inner class VH(val binding: ItemClimaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Clima) {
            binding.tvCity.text = item.cityName
            binding.tvTemp.text = "${item.temperature} ${getUnitSymbol()}"
            binding.tvDesc.text = item.description

            // Actualizar hora actual
            updateCurrentTime()

            binding.checkSelect.visibility =
                if (isSelectionMode) View.VISIBLE else View.GONE

            binding.checkSelect.isChecked = selectedItems.any { it.id == item.id }

            // Usar listener null-safe para evitar llamadas múltiples
            binding.checkSelect.setOnCheckedChangeListener(null)
            binding.checkSelect.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    selectedItems.add(item)
                } else {
                    selectedItems.removeIf { it.id == item.id }
                }
            }

            binding.root.setOnClickListener {
                if (!isSelectionMode) {
                    onItemClick(item)
                } else {
                    val newCheckedState = !binding.checkSelect.isChecked
                    binding.checkSelect.isChecked = newCheckedState
                }
            }

            binding.root.setOnLongClickListener {
                if (!isSelectionMode) {
                    isSelectionMode = true
                    notifyDataSetChanged()
                    true
                } else {
                    false
                }
            }
        }

        fun updateCurrentTime() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentTime = System.currentTimeMillis()
            val formattedDate = sdf.format(Date(currentTime))
            binding.tvTs.text = "$formattedDate (UTC+3)"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemClimaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    // Manejar actualizaciones específicas para optimizar rendimiento
    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == "time_update") {
            // Solo actualizar la hora
            holder.updateCurrentTime()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun update(newItems: List<Clima>) {
        items = newItems
        notifyDataSetChanged()
    }

    // Funciones para manejar actualizaciones de tiempo en tiempo real
    fun startTimeUpdates() {
        if (isTimeUpdatesRunning) return

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Notificar cambios solo en la vista de tiempo
                notifyItemRangeChanged(0, itemCount, "time_update")
            }
        }, 0, 60000) // Actualizar cada minuto (60000 ms)

        isTimeUpdatesRunning = true
    }

    fun stopTimeUpdates() {
        timer?.cancel()
        timer = null
        isTimeUpdatesRunning = false
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        stopTimeUpdates()
    }


    fun clearSelection() {
        selectedItems.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Clima> {
        return selectedItems.toList()
    }

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun getUnitSymbol(): String {
        return if (units == "imperial") "°F" else "°C"
    }

    fun updateUnits(newUnits: String) {
        units = newUnits
        notifyDataSetChanged()
    }

}