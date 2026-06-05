package com.uth.gestionusuarios.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.uth.gestionusuarios.models.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuariosDB {
    private DbHelper dbHelper;
    private Context context;

    public UsuariosDB(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            // Filtrar por status = 1 (Activo)
            Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_CLIENTES + 
                " WHERE " + DbHelper.COLUMN_STATUS + " = 1", null);

            if (cursor.moveToFirst()) {
                do {
                    Usuario usuario = new Usuario();
                    usuario.setId(cursor.getInt(0));
                    usuario.setNombre(cursor.getString(1));
                    usuario.setCorreo(cursor.getString(2));
                    usuario.setTelefono(cursor.getString(3));
                    usuario.setEdad(cursor.getInt(4));
                    usuario.setFotoBase64(cursor.getString(5));
                    usuario.setStatus(cursor.getInt(6));
                    lista.add(usuario);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Usuario consultar(int id) {
        Usuario usuario = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_CLIENTES + 
                " WHERE " + DbHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                usuario = new Usuario();
                usuario.setId(cursor.getInt(0));
                usuario.setNombre(cursor.getString(1));
                usuario.setCorreo(cursor.getString(2));
                usuario.setTelefono(cursor.getString(3));
                usuario.setEdad(cursor.getInt(4));
                usuario.setFotoBase64(cursor.getString(5));
                usuario.setStatus(cursor.getInt(6));
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return usuario;
    }

    public long insertar(Usuario usuario) {
        long id = 0;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_NOMBRE, usuario.getNombre());
            values.put(DbHelper.COLUMN_CORREO, usuario.getCorreo());
            values.put(DbHelper.COLUMN_TELEFONO, usuario.getTelefono());
            values.put(DbHelper.COLUMN_EDAD, usuario.getEdad());
            values.put(DbHelper.COLUMN_FOTO, usuario.getFotoBase64());
            values.put(DbHelper.COLUMN_STATUS, 1); // Activo por defecto

            id = db.insert(DbHelper.TABLE_CLIENTES, null, values);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    public boolean actualizar(Usuario usuario) {
        boolean exito = false;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_NOMBRE, usuario.getNombre());
            values.put(DbHelper.COLUMN_CORREO, usuario.getCorreo());
            values.put(DbHelper.COLUMN_TELEFONO, usuario.getTelefono());
            values.put(DbHelper.COLUMN_EDAD, usuario.getEdad());
            if (usuario.getFotoBase64() != null) {
                values.put(DbHelper.COLUMN_FOTO, usuario.getFotoBase64());
            }

            int filas = db.update(DbHelper.TABLE_CLIENTES, values, 
                DbHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(usuario.getId())});
            
            exito = filas > 0;
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return exito;
    }

    public boolean eliminar(int id) {
        boolean exito = false;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_STATUS, 0); // Inactivo (Borrado lógico)

            int filas = db.update(DbHelper.TABLE_CLIENTES, values, 
                DbHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            
            exito = filas > 0;
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return exito;
    }
}
