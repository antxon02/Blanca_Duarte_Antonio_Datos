package com.antonio.blanca_duarte_antonio_datos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn1 = findViewById<android.widget.Button>(R.id.btnEjercicio1)

        btn1.setOnClickListener {
            val intent = Intent(this, Ejercicio1Activity::class.java)
            startActivity(intent)
        }

        val btn2 = findViewById<android.widget.Button>(R.id.btnEjercicio2)

        btn2.setOnClickListener {
            val intent = Intent(this, Ejercicio2Activity::class.java)
            startActivity(intent)
        }

        val btn3 = findViewById<android.widget.Button>(R.id.btnEjercicio3)

        btn3.setOnClickListener {
            val intent = Intent(this, Ejercicio3Activity::class.java)
            startActivity(intent)
        }
    }
}