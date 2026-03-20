package com.antonio.blanca_duarte_antonio_datos.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.antonio.blanca_duarte_antonio_datos.R

class ToolsAdapter(
    var lista: MutableList<ToolIA>,
    private val onClick: (ToolIA) -> Unit,
    private val onLongClick: (ToolIA) -> Unit
) : RecyclerView.Adapter<ToolsAdapter.ToolViewHolder>() {

    class ToolViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvCat: TextView = view.findViewById(R.id.tvCategoria)
        val ivIcono: android.widget.ImageView = view.findViewById(R.id.ivIcono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tool, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.nombre
        holder.tvDesc.text = item.descripcion
        holder.tvCat.text = item.categoria

        holder.ivIcono.setImageResource(item.imagen)

        holder.itemView.setOnClickListener { onClick(item) }

        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun getItemCount() = lista.size
}