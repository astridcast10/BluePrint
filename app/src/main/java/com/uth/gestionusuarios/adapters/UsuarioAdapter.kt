package com.uth.gestionusuarios.adapters

import android.content.Context
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

        imgUsuario.setImageResource(android.R.drawable.ic_menu_myplaces)

        return view
    }

    fun actualizarLista(nuevaLista: List<Usuario>) {
        usuarios = nuevaLista
        notifyDataSetChanged()
    }
}