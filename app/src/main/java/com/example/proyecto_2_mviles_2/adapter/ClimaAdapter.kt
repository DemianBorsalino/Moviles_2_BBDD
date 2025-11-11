package com.example.proyecto_2_mviles_2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_2_mviles_2.databinding.ItemClimaBinding
import com.example.proyecto_2_mviles_2.model.Clima
import java.text.SimpleDateFormat
import java.util.*

class ClimaAdapter(private var items: List<Clima>, private val onItemClick: (Clima) -> Unit)
    : RecyclerView.Adapter<ClimaAdapter.VH>() {

    inner class VH(private val binding: ItemClimaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Clima) {
            binding.tvCity.text = item.cityName
            binding.tvTemp.text = "${item.temperature} °C"
            binding.tvDesc.text = item.description
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            binding.tvTs.text = sdf.format(Date(item.timestamp))
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemClimaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Clima>) {
        items = newItems
        notifyDataSetChanged()
    }
}
