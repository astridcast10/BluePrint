package com.uth.gestionusuarios.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uth.blueprint.R

// Modelo de datos simple para estructurar la información del usuario
data class Usuario(
    val nombre: String,
    val edad: String,
    val email: String,
    val telefono: String
)

class DetalleUsuarioActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEdad: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var tilNombre: TextInputLayout

    // Launcher galería
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data!!.data
            imgAvatar.setImageURI(imageUri)
            imgAvatar.setPadding(0, 0, 0, 0)
            imgAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Launcher cámara
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val photo = result.data!!.extras?.get("data") as? Bitmap
            imgAvatar.setImageBitmap(photo)
            imgAvatar.setPadding(0, 0, 0, 0)
            imgAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Launcher permiso cámara
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) abrirCamara()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_usuario)

        // Referencias
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        val fabCamera = findViewById<FloatingActionButton>(R.id.fabCamera)
        val btnClean = findViewById<MaterialButton>(R.id.btnClean)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)

        imgAvatar  = findViewById(R.id.imgAvatar)
        etNombre   = findViewById(R.id.etNombre)
        etEdad     = findViewById(R.id.etEdad)
        etEmail    = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        tilNombre  = findViewById(R.id.tilNombre)

        // Botón regresar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Hint "Nombre *" con asterisco rojo
        val hint = SpannableString("Nombre *")
        hint.setSpan(
            ForegroundColorSpan(0xFFD32F2F.toInt()),
            7, 8,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tilNombre.hint = hint

        // Cámara / galería
        fabCamera.setOnClickListener { mostrarDialogoFoto() }

        // Limpiar
        btnClean.setOnClickListener { limpiarCampos() }

        // Guardar
        btnSave.setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim() ?: ""
            if (nombre.isEmpty()) {
                tilNombre.error = "El nombre es obligatorio"
                return@setOnClickListener
            }
            tilNombre.error = null

            // 1. Capturar el resto de la información ya limpia (.trim())
            val edad = etEdad.text?.toString()?.trim() ?: ""
            val email = etEmail.text?.toString()?.trim() ?: ""
            val telefono = etTelefono.text?.toString()?.trim() ?: ""

            // 2. Agrupar los datos en nuestra estructura/modelo
            val nuevoUsuario = Usuario(nombre, edad, email, telefono)

            // 3. Ejecutar la acción de guardado
            guardarEnBaseDeDatos(nuevoUsuario)
        }

        // Eliminar
        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de que deseas eliminar este usuario?")
                .setPositiveButton("Eliminar") { _, _ ->
                    // TODO: eliminar de base de datos
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun guardarEnBaseDeDatos(usuario: Usuario) {
        // En este espacio procesas el objeto 'usuario' según la arquitectura de tu app.
        // Por ejemplo: miViewModel.insertarUsuario(usuario) o mDatabase.child("usuarios").push().setValue(usuario)

        // Mostramos confirmación en pantalla
        Toast.makeText(this, "Usuario '${usuario.nombre}' guardado con éxito", Toast.LENGTH_SHORT).show()

        // Finalizamos la actividad para retornar a la pantalla anterior
        finish()
    }

    private fun mostrarDialogoFoto() {
        AlertDialog.Builder(this)
            .setTitle("Seleccionar foto")
            .setItems(arrayOf("Tomar foto con cámara", "Elegir de galería")) { _, which ->
                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        abrirCamara()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                } else {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryLauncher.launch(intent)
                }
            }
            .show()
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun limpiarCampos() {
        etNombre.setText("")
        etEdad.setText("")
        etEmail.setText("")
        etTelefono.setText("")
        tilNombre.error = null
        imgAvatar.setImageResource(android.R.drawable.ic_menu_myplaces)
        imgAvatar.setPadding(10, 10, 10, 10)
        imgAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
    }
}