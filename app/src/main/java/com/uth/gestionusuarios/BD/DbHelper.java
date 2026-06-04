package com.uth.gestionusuarios.BD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BluePrint.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla
    public static final String TABLE_CLIENTES = "clientes";

    // Columnas de la tabla
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";

    public static final String COLUMN_CORREO = "correo";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_EDAD = "edad";

    public static final String COLUMN_FOTO = "foto_base64";

    // Sentencia SQL para crear la tabla
    private static final String CREATE_TABLE_CLIENTES = 
            "CREATE TABLE " + TABLE_CLIENTES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NOMBRE + " TEXT UNIQUE NOT NULL, " +
            COLUMN_CORREO + " TEXT UNIQUE, " +
            COLUMN_TELEFONO + " TEXT UNIQUE, " +
            COLUMN_EDAD + " INTEGER NOT NULL, " +
            COLUMN_FOTO + " TEXT);";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En una aplicación real, aquí manejarías la migración
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTES);
        onCreate(db);
    }
}
