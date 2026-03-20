package com.antonio.blanca_duarte_antonio_datos.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQL(context: Context) : SQLiteOpenHelper(context, "BaseFinalIA", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE herramientas (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, descripcion TEXT, url TEXT, categoria TEXT, imagen INTEGER)"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS herramientas")
        onCreate(db)
    }

    fun agregarHerramienta(tool: ToolIA): Long {
        val db = this.writableDatabase

        val values = ContentValues()

        values.put("nombre", tool.nombre)
        values.put("descripcion", tool.descripcion)
        values.put("url", tool.url)
        values.put("categoria", tool.categoria)
        values.put("imagen", tool.imagen)

        val id = db.insert("herramientas", null, values)
        db.close()
        return id
    }

    fun obtenerHerramientas(): MutableList<ToolIA> {
        val lista = mutableListOf<ToolIA>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM herramientas", null)

        if (cursor.moveToFirst()) {
            do {
                val tool = ToolIA(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    descripcion = cursor.getString(2),
                    url = cursor.getString(3),
                    categoria = cursor.getString(4),
                    imagen = cursor.getInt(5)
                )

                lista.add(tool)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lista
    }

    fun editarHerramienta(tool: ToolIA) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("nombre", tool.nombre)
        values.put("descripcion", tool.descripcion)
        values.put("url", tool.url)
        values.put("categoria", tool.categoria)
        values.put("imagen", tool.imagen)

        db.update("herramientas", values, "id=?", arrayOf(tool.id.toString()))
        db.close()
    }

    fun borrarHerramienta(id: Int) {
        val db = this.writableDatabase
        db.delete("herramientas", "id=?", arrayOf(id.toString()))
        db.close()
    }
}