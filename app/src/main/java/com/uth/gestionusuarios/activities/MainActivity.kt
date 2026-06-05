package com.uth.gestionusuarios.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.uth.blueprint.R
import com.uth.gestionusuarios.BD.UsuariosDB
import com.uth.gestionusuarios.adapters.UsuarioAdapter
import com.uth.gestionusuarios.models.Usuario

class MainActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_MODO = "modo"
        const val EXTRA_ID_USUARIO = "id_usuario"

        const val MODO_CREAR = "crear"
        const val MODO_VER = "ver"

    }

    private lateinit var etBuscar: EditText
    private lateinit var tvContador: TextView

    private lateinit var tvMostrando: TextView
    private lateinit var tvMensaje: TextView
    private lateinit var listView: ListView
    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: UsuarioAdapter
    private lateinit var usuariosDB: UsuariosDB

    private var textoBusqueda = ""

    private var usuarios = listOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )

        usuariosDB = UsuariosDB(this)

        inicializar()

        configurarLista()

        configurarBusqueda()

        configurarFAB()

        cargarUsuarios()

    }

    override fun onResume() {

        super.onResume()

        cargarUsuarios()

    }

    private fun inicializar() {

        etBuscar =
            findViewById(R.id.etBuscar)

        tvContador =
            findViewById(R.id.tvContador)

        tvMensaje =
            findViewById(R.id.tvMensaje)

        listView =
            findViewById(R.id.listViewUsuarios)

        fab =
            findViewById(R.id.fabAgregar)

        tvMostrando =
            findViewById(R.id.tvMostrando)

    }

    private fun configurarLista() {

        adapter =
            UsuarioAdapter(
                this,
                emptyList()
            )

        listView.adapter =
            adapter

        listView.setOnItemClickListener {

                _,
                _,
                position,
                _ ->

            val usuario =
                adapter.getItem(position)

            val intent =
                Intent(
                    this,
                    DetalleUsuarioActivity::class.java
                )

            intent.putExtra(
                EXTRA_MODO,
                MODO_VER
            )

            intent.putExtra(
                EXTRA_ID_USUARIO,
                usuario.id
            )

            startActivity(intent)

        }

    }

    private fun configurarFAB() {

        fab.setOnClickListener {

            val intent =
                Intent(
                    this,
                    GuardarUsuarioActivity::class.java
                )

            startActivity(intent)

        }

    }

    private fun configurarBusqueda() {

        etBuscar.setText(
            textoBusqueda
        )

        etBuscar.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    textoBusqueda =
                        s.toString()

                    cargarUsuarios()

                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }

            }

        )

    }

    private fun cargarUsuarios() {

        usuarios = usuariosDB.listar()

        val lista =

            if (
                textoBusqueda.isBlank()
            ) {

                usuarios

            } else {

                usuarios.filter {

                    it.nombre.contains(
                        textoBusqueda,
                        true
                    )

                }

            }

        adapter.actualizarLista(
            lista
        )

        actualizarEstado(
            lista.size
        )

    }

    private fun actualizarEstado(
        cantidad: Int
    ) {

        tvContador.text =
            "Clientes"

        tvMostrando.text =
            "Mostrando $cantidad de ${usuarios.size}"

        when {

            usuarios.isEmpty() -> {

                tvMensaje.visibility =
                    View.VISIBLE

                tvMensaje.text =
                    "No hay usuarios registrados"

                listView.visibility =
                    View.GONE

            }

            cantidad == 0 -> {

                tvMensaje.visibility =
                    View.VISIBLE

                tvMensaje.text =
                    "No se encontraron usuarios"

                listView.visibility =
                    View.GONE

            }

            else -> {

                tvMensaje.visibility =
                    View.GONE

                listView.visibility =
                    View.VISIBLE

            }

        }

    }

}