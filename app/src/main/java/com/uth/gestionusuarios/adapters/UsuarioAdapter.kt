package com.uth.gestionusuarios.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.uth.blueprint.R
import com.uth.gestionusuarios.models.Usuario

class UsuarioAdapter(
    private val context: Context,
    private var usuarios: List<Usuario>
) : BaseAdapter() {

    override fun getCount(): Int = usuarios.size

    override fun getItem(position: Int): Usuario = usuarios[position]

    override fun getItemId(position: Int): Long = usuarios[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater
            .from(context)
            .inflate(R.layout.item_usuario, parent, false)

        val usuario = usuarios[position]

        val imgUsuario = view.findViewById<ImageView>(R.id.imgUsuario)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvCorreo = view.findViewById<TextView>(R.id.tvCorreo)
        val tvTelefono = view.findViewById<TextView>(R.id.tvEdad)

        tvNombre.text = usuario.nombre
        tvCorreo.text = usuario.correo ?: "Sin correo"
        tvTelefono.text = usuario.telefono ?: "Sin teléfono"

        if (!usuario.fotoBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(usuario.fotoBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imgUsuario.setImageBitmap(decodedImage)
                imgUsuario.setPadding(0, 0, 0, 0)
            } catch (e: Exception) {
                imgUsuario.setImageResource(android.R.drawable.sym_def_app_icon)
                imgUsuario.setPadding(6, 6, 6, 6)
            }
        } else {
            imgUsuario.setImageResource(android.R.drawable.sym_def_app_icon)
            imgUsuario.setPadding(6, 6, 6, 6)
        }

        return view
    }

    fun actualizarLista(nuevaLista: List<Usuario>) {
        usuarios = nuevaLista
        notifyDataSetChanged()
    }
}