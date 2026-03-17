package com.antonio.blanca_duarte_antonio_datos

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.antonio.blanca_duarte_antonio_datos.databinding.ActivityEjercicio1Binding

class Ejercicio1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityEjercicio1Binding

    private lateinit var sharedPrefs: SharedPreferences

    private var ratioConversion = 0.92

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicio1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        sharedPrefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        cargarPreferencias()

        binding.etCantidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calcularConversion()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.rgOpciones.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbDolarToEuro) {
                binding.tvMonedaLabel.text = "Euros"
            } else {
                binding.tvMonedaLabel.text = "Dólares"
            }
            calcularConversion()
        }
    }

    private fun calcularConversion() {
        val textoInput = binding.etCantidad.text.toString()

        val cantidad = textoInput.toDoubleOrNull() ?: 0.0

        var resultado = 0.0

        if (binding.rbDolarToEuro.isChecked) {
            resultado = cantidad * ratioConversion
        } else {
            if (ratioConversion != 0.0) {
                resultado = cantidad / ratioConversion
            }
        }

        binding.tvResultado.text = String.format("%.2f", resultado)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_conversor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.color_azul -> cambiarColor("#E3F2FD")
            R.id.color_verde -> cambiarColor("#E8F5E9")
            R.id.color_amarillo -> cambiarColor("#FFFDE7")
            R.id.color_blanco -> cambiarColor("#FFFFFF")

            R.id.menu_ratio -> mostrarDialogoRatio()

            R.id.menu_acerca -> {
                Toast.makeText(this, "Creado por: Tu Nombre \nVersión 1.0", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun cambiarColor(hexColor: String) {
        val colorInt = Color.parseColor(hexColor)
        binding.mainLayout.setBackgroundColor(colorInt)

        sharedPrefs.edit().putString("color_fondo", hexColor).apply()
    }

    private fun mostrarDialogoRatio() {
        val inputEdit = EditText(this)
        inputEdit.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        inputEdit.hint = "Actual: $ratioConversion"

        AlertDialog.Builder(this)
            .setTitle("Cambiar Ratio")
            .setMessage("Introduce cuántos Euros son 1 Dólar")
            .setView(inputEdit)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoRatio = inputEdit.text.toString().toDoubleOrNull()
                if (nuevoRatio != null && nuevoRatio > 0) {
                    ratioConversion = nuevoRatio
                    sharedPrefs.edit().putFloat("ratio_guardado", ratioConversion.toFloat()).apply()
                    calcularConversion()
                    Toast.makeText(this, "Ratio actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargarPreferencias() {
        val colorGuardado = sharedPrefs.getString("color_fondo", "#FFFFFF")
        try {
            binding.mainLayout.setBackgroundColor(Color.parseColor(colorGuardado))
        } catch (e: Exception) { }


        val ratioGuardado = sharedPrefs.getFloat("ratio_guardado", 0.92f)
        ratioConversion = ratioGuardado.toDouble()
    }
}