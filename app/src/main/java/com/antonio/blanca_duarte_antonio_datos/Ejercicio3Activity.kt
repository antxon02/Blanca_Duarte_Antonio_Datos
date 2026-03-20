package com.antonio.blanca_duarte_antonio_datos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.blanca_duarte_antonio_datos.data.AdminSQL
import com.antonio.blanca_duarte_antonio_datos.data.ToolIA
import com.antonio.blanca_duarte_antonio_datos.data.ToolsAdapter
import com.antonio.blanca_duarte_antonio_datos.databinding.ActivityEjercicio3Binding

class Ejercicio3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityEjercicio3Binding
    private lateinit var db: AdminSQL
    private lateinit var adapter: ToolsAdapter
    private var listaTools: MutableList<ToolIA> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicio3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AdminSQL(this)

        setupRecyclerView()
        cargarDatos()

        binding.fabAdd.setOnClickListener {
            mostrarDialogo(null)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ToolsAdapter(listaTools,
            onClick = { tool ->
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tool.url))
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Enlace inválido", Toast.LENGTH_SHORT).show()
                }
            },
            onLongClick = { tool ->
                mostrarDialogo(tool)
            }
        )
        binding.recyclerView.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val toolABorrar = listaTools[position]

                db.borrarHerramienta(toolABorrar.id)

                listaTools.removeAt(position)

                adapter.notifyItemRemoved(position)

                Toast.makeText(applicationContext, "Eliminado", Toast.LENGTH_SHORT).show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun cargarDatos() {
        listaTools.clear()
        listaTools.addAll(db.obtenerHerramientas())
        adapter.notifyDataSetChanged()
    }

    private fun mostrarDialogo(tool: ToolIA?) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_tool, null)

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etDesc = view.findViewById<EditText>(R.id.etDesc)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)
        val etCat = view.findViewById<EditText>(R.id.etCat)

        if (tool != null) {
            etNombre.setText(tool.nombre)
            etDesc.setText(tool.descripcion)
            etUrl.setText(tool.url)
            etCat.setText(tool.categoria)
            builder.setTitle("Editar Herramienta")
        } else {
            builder.setTitle("Nueva Herramienta")
        }

        builder.setView(view)
        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevoNombre = etNombre.text.toString()
            val nuevaDesc = etDesc.text.toString()
            var nuevaUrl = etUrl.text.toString()
            val nuevaCat = etCat.text.toString()

            if (!nuevaUrl.startsWith("http")) nuevaUrl = "https://$nuevaUrl"

            if (tool == null) {

                val nuevaTool = ToolIA(0, nuevoNombre, nuevaDesc, nuevaUrl, nuevaCat)
                db.agregarHerramienta(nuevaTool)
            } else {

                tool.nombre = nuevoNombre
                tool.descripcion = nuevaDesc
                tool.url = nuevaUrl
                tool.categoria = nuevaCat
                db.editarHerramienta(tool)
            }
            cargarDatos()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}