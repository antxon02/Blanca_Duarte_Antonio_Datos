import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonio.blanca_duarte_antonio_datos.R
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
                if (tool.url.isNotEmpty()) {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tool.url))
                        startActivity(browserIntent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error al abrir enlace: ${tool.url}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Esta herramienta no tiene enlace", Toast.LENGTH_SHORT).show()
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

                Toast.makeText(applicationContext, "Herramienta eliminada", Toast.LENGTH_SHORT).show()
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
        val rgIconos = view.findViewById<RadioGroup>(R.id.rgIconos)

        if (tool != null) {
            builder.setTitle("Editar Herramienta")
            etNombre.setText(tool.nombre)
            etDesc.setText(tool.descripcion)
            etUrl.setText(tool.url)
            etCat.setText(tool.categoria)

            when (tool.imagen) {
                android.R.drawable.ic_menu_my_calendar -> rgIconos.check(R.id.rb2)
                android.R.drawable.ic_menu_compass -> rgIconos.check(R.id.rb3)
                else -> rgIconos.check(R.id.rb1)
            }
        } else {
            builder.setTitle("Nueva Herramienta")
        }

        builder.setView(view)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevoNombre = etNombre.text.toString()
            val nuevaDesc = etDesc.text.toString()
            var nuevaUrl = etUrl.text.toString()
            val nuevaCat = etCat.text.toString()

            if (nuevaUrl.isNotEmpty() && !nuevaUrl.startsWith("http")) {
                nuevaUrl = "https://$nuevaUrl"
            }

            val idIconoSeleccionado = when (rgIconos.checkedRadioButtonId) {
                R.id.rb2 -> android.R.drawable.ic_menu_my_calendar
                R.id.rb3 -> android.R.drawable.ic_menu_compass
                else -> android.R.drawable.sym_def_app_icon
            }

            if (tool == null) {
                val nuevaTool = ToolIA(0, nuevoNombre, nuevaDesc, nuevaUrl, nuevaCat, idIconoSeleccionado)
                db.agregarHerramienta(nuevaTool)
                Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                tool.nombre = nuevoNombre
                tool.descripcion = nuevaDesc
                tool.url = nuevaUrl
                tool.categoria = nuevaCat
                tool.imagen = idIconoSeleccionado

                db.editarHerramienta(tool)
                Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
            
            cargarDatos()
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}