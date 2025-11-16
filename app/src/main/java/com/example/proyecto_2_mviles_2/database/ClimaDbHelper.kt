package com.example.proyecto_2_mviles_2.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyecto_2_mviles_2.model.Clima

class ClimaDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "clima.db"
        private const val DB_VERSION = 1

        const val TABLE_NAME = "climas"
        const val COL_ID = "id"
        const val COL_CITY = "city"
        const val COL_DESC = "description"
        const val COL_TEMP = "temperature"
        const val COL_TS = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CITY TEXT NOT NULL,
                $COL_DESC TEXT,
                $COL_TEMP REAL,
                $COL_TS INTEGER
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertClima(clima: Clima) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_CITY, clima.cityName)
            put(COL_DESC, clima.description)
            put(COL_TEMP, clima.temperature)
            put(COL_TS, clima.timestamp)
        }
        db.insert(TABLE_NAME, null, cv)
        db.close()
    }

    fun getAllClimas(): List<Clima> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COL_TS DESC")
        val list = mutableListOf<Clima>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Clima(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        cityName = it.getString(it.getColumnIndexOrThrow(COL_CITY)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_DESC)),
                        temperature = it.getDouble(it.getColumnIndexOrThrow(COL_TEMP)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_TS))
                    )
                )
            }
        }
        db.close()
        return list
    }

    fun deleteClima(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteMultipleClimas(ids: List<Int>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            ids.forEach { id ->
                db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }
    fun deleteAllClimas() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

}
