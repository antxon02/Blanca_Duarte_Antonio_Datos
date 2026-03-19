package com.antonio.blanca_duarte_antonio_datos

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.antonio.blanca_duarte_antonio_datos.databinding.ActivityEjercicio2Binding
import java.io.File
import java.util.Queue
import java.util.LinkedList

data class AlarmaDatos(
    val minutos: Int,
    val mensaje: String,
    val sonido: String
)

class Ejercicio2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityEjercicio2Binding
    private lateinit var sharedPrefs: SharedPreferences

    // Cola de alarmas (FIFO: First In, First Out)
    private val colaAlarmas: Queue<AlarmaDatos> = LinkedList()
    private var timerActual: CountDownTimer? = null

    // Variables de configuración
    private var nombreFichero = "alarmas.txt"
    private var sonidoActivado = true
    private val CHANNEL_ID = "canal_alarmas_fit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicio2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        crearCanalNotificaciones()
        sharedPrefs = getSharedPreferences("PrefsAlarmas", Context.MODE_PRIVATE)
        cargarPreferencias()

        // Cargar contenido del fichero en el EditText al iniciar
        leerFicheroYMostrar()

        binding.btnGuardarYArrancar.setOnClickListener {
            if (guardarFicheroDesdeEdit()) {
                prepararAlarmas()
                lanzarSiguienteAlarma()
            }
        }

        // Pedir permiso de notificaciones si es Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    // --- LÓGICA DEL TEMPORIZADOR ---

    private fun prepararAlarmas() {
        colaAlarmas.clear()
        // Parsear lo que hay en el EditText para llenar la cola
        val lineas = binding.etFicheroContenido.text.toString().split("\n")

        for (linea in lineas) {
            val partes = linea.split(",")
            if (partes.size >= 2) {
                try {
                    val min = partes[0].trim().toInt()
                    val msg = partes[1].trim()
                    val sound = if (partes.size > 2) partes[2].trim() else "default"

                    // Limitar a 5 alarmas máximo según enunciado
                    if (colaAlarmas.size < 5) {
                        colaAlarmas.add(AlarmaDatos(min, msg, sound))
                    }
                } catch (e: Exception) {
                    // Ignorar líneas mal formadas
                }
            }
        }
        binding.tvAlarmasRestantes.text = "Alarmas pendientes: ${colaAlarmas.size}"
    }

    private fun lanzarSiguienteAlarma() {
        if (colaAlarmas.isEmpty()) {
            binding.tvTiempoRestante.text = "FIN"
            binding.tvAlarmasRestantes.text = "Entrenamiento completado"
            return
        }

        val alarmaActual = colaAlarmas.poll() // Sacar la siguiente alarma
        binding.tvAlarmasRestantes.text = "Alarmas pendientes: ${colaAlarmas.size + 1}" // +1 porque la actual está corriendo

        // Convertir minutos a milisegundos
        // PARA PRUEBAS RÁPIDAS: Multiplicar por 1000 (segundos) en vez de 60000 (minutos)
        val tiempoMilis = alarmaActual!!.minutos * 1000L // * 60000L para minutos reales

        timerActual = object : CountDownTimer(tiempoMilis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seg = (millisUntilFinished / 1000) % 60
                val min = (millisUntilFinished / 1000) / 60
                binding.tvTiempoRestante.text = String.format("%02d:%02d", min, seg)
            }

            override fun onFinish() {
                ejecutarAlarma(alarmaActual)
                lanzarSiguienteAlarma() // RECURSIVIDAD: Lanza la siguiente
            }
        }.start()
    }

    private fun ejecutarAlarma(alarma: AlarmaDatos) {
        // 1. Sonido
        if (sonidoActivado) {
            try {
                val notifSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val mp = MediaPlayer.create(applicationContext, notifSound)
                mp.start()
            } catch (e: Exception) { }
        }

        // 2. Notificación
        mostrarNotificacion(alarma.mensaje)
        Toast.makeText(this, "ALARMA: ${alarma.mensaje}", Toast.LENGTH_LONG).show()
    }

    // --- FICHEROS ---

    private fun leerFicheroYMostrar() {
        val fichero = File(getExternalFilesDir(null), nombreFichero)
        if (fichero.exists()) {
            binding.etFicheroContenido.setText(fichero.readText())
        } else {
            // Texto por defecto para probar
            val demo = "1, Calentamiento, sound1\n2, Carrera suave, sound2\n1, Sprint, sound3"
            binding.etFicheroContenido.setText(demo)
        }
    }

    private fun guardarFicheroDesdeEdit(): Boolean {
        return try {
            val fichero = File(getExternalFilesDir(null), nombreFichero)
            fichero.writeText(binding.etFicheroContenido.text.toString())
            Toast.makeText(this, "Guardado en $nombreFichero", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            false
        }
    }

    // --- NOTIFICACIONES ---

    private fun mostrarNotificacion(mensaje: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Alarma Deportiva")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarmas Ejercicio"
            val descriptionText = "Canal para secuencia de alarmas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // --- MENÚ Y PREFERENCIAS ---

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_alarmas, menu)
        val itemSonido = menu?.findItem(R.id.menu_sonido_on)
        itemSonido?.isChecked = sonidoActivado
        itemSonido?.title = if (sonidoActivado) "Sonido: ON" else "Sonido: OFF"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sonido_on -> {
                sonidoActivado = !sonidoActivado
                item.isChecked = sonidoActivado
                item.title = if (sonidoActivado) "Sonido: ON" else "Sonido: OFF"
                sharedPrefs.edit().putBoolean("sound_on", sonidoActivado).apply()
            }
            R.id.menu_nombre_fichero -> {
                dialogoCambiarFichero()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogoCambiarFichero() {
        val input = EditText(this)
        input.setText(nombreFichero)
        AlertDialog.Builder(this)
            .setTitle("Nombre del fichero")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                nombreFichero = input.text.toString()
                if (!nombreFichero.endsWith(".txt")) nombreFichero += ".txt"
                sharedPrefs.edit().putString("filename", nombreFichero).apply()
                leerFicheroYMostrar() // Recargar si cambiamos de fichero
            }
            .show()
    }

    private fun cargarPreferencias() {
        sonidoActivado = sharedPrefs.getBoolean("sound_on", true)
        nombreFichero = sharedPrefs.getString("filename", "alarmas.txt") ?: "alarmas.txt"
    }

    override fun onDestroy() {
        super.onDestroy()
        timerActual?.cancel()
    }
}
