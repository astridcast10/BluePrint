package com.uth.gestionusuarios.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Base64
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
import com.uth.gestionusuarios.BD.UsuariosDB
import com.uth.gestionusuarios.models.Usuario
import java.io.ByteArrayOutputStream

class DetalleUsuarioActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var etId: TextInputEditText
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEdad: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilEdad: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout

    private lateinit var usuariosDB: UsuariosDB
    private var idUsuario: Int = 0

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

        usuariosDB = UsuariosDB(this)
        idUsuario = intent.getIntExtra(MainActivity.EXTRA_ID_USUARIO, 0)

        // Referencias
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        val fabCamera = findViewById<FloatingActionButton>(R.id.fabCamera)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)

        imgAvatar  = findViewById(R.id.imgAvatar)
        etId       = findViewById(R.id.etId)
        etNombre   = findViewById(R.id.etNombre)
        etEdad     = findViewById(R.id.etEdad)
        etEmail    = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        tilNombre  = findViewById(R.id.tilNombre)
        tilEdad    = findViewById(R.id.tilEdad)
        tilEmail   = findViewById(R.id.tilEmail)
        tilTelefono = findViewById(R.id.tilTelefono)

        cargarDatosUsuario()

        // Botón regresar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Hint "Nombre *" con asterisco rojo
        val hintNombre = SpannableString("Nombre completo *")
        hintNombre.setSpan(
            ForegroundColorSpan(0xFFD32F2F.toInt()),
            16, 17,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tilNombre.hint = hintNombre

        val hintEdad = SpannableString("Edad *")
        hintEdad.setSpan(
            ForegroundColorSpan(0xFFD32F2F.toInt()),
            5, 6,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tilEdad.hint = hintEdad

        // Cámara / galería
        fabCamera.setOnClickListener { mostrarDialogoFoto() }

        // Guardar (Actualizar)
        btnSave.setOnClickListener {
            if (!validarCampos()) return@setOnClickListener

            val id = etId.text?.toString()?.toIntOrNull() ?: 0
            val nombre = etNombre.text?.toString()?.trim() ?: ""
            val edad = etEdad.text?.toString()?.toIntOrNull() ?: 0
            
            var email = etEmail.text?.toString()?.trim() ?: ""
            if (email.isEmpty()) email = "N/A"
            
            var telefono = etTelefono.text?.toString()?.trim() ?: ""
            if (telefono.isEmpty()) telefono = "N/A"

            val fotoBase64 = imagenToBase64()

            val usuarioEditado = Usuario(id, nombre, email, telefono, edad, fotoBase64)

            actualizarEnBaseDeDatos(usuarioEditado)
        }

        // Eliminar
        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de que deseas eliminar este usuario?")
                .setPositiveButton("Eliminar") { _, _ ->
                    val exito = usuariosDB.eliminar(idUsuario)
                    if (exito) {
                        Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        val nombre = etNombre.text.toString().trim()
        if (nombre.isEmpty()) {
            tilNombre.error = "El nombre es obligatorio"
            esValido = false
        } else {
            tilNombre.error = null
        }

        val edadStr = etEdad.text.toString().trim()
        val edad = edadStr.toIntOrNull()
        if (edadStr.isEmpty()) {
            tilEdad.error = "La edad es obligatoria"
            esValido = false
        } else if (edad == null || edad < 0 || edad > 100) {
            tilEdad.error = "Edad no permitida (0 - 100)"
            esValido = false
        } else {
            tilEdad.error = null
        }

        val email = etEmail.text.toString().trim()
        if (email.isNotEmpty() && email != "N/A" && !email.contains("@")) {
            tilEmail.error = "Correo inválido"
            esValido = false
        } else {
            tilEmail.error = null
        }

        val telefono = etTelefono.text.toString().trim()
        if (telefono.isNotEmpty() && telefono != "N/A" && !Regex("^\\d{4}-\\d{4}$").matches(telefono)) {
            tilTelefono.error = "Formato inválido (####-####)"
            esValido = false
        } else {
            tilTelefono.error = null
        }

        return esValido
    }

    private fun cargarDatosUsuario() {
        val usuario = usuariosDB.consultar(idUsuario)
        if (usuario != null) {
            etId.setText(usuario.id.toString())
            etNombre.setText(usuario.nombre)
            etEdad.setText(usuario.edad.toString())
            etEmail.setText(usuario.correo)
            etTelefono.setText(usuario.telefono)

            if (!usuario.fotoBase64.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(usuario.fotoBase64, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imgAvatar.setImageBitmap(decodedImage)
                    imgAvatar.setPadding(0, 0, 0, 0)
                    imgAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
                } catch (e: Exception) {
                    imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
                    imgAvatar.setPadding(6, 6, 6, 6)
                }
            } else {
                imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
                imgAvatar.setPadding(6, 6, 6, 6)
            }
        } else {
            Toast.makeText(this, "No se pudo cargar la información del usuario", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun actualizarEnBaseDeDatos(usuario: Usuario) {
        val exito = usuariosDB.actualizar(usuario)
        if (exito) {
            Toast.makeText(this, "Usuario '${usuario.nombre}' actualizado con éxito", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imagenToBase64(): String {
        return try {
            val bitmap = (imgAvatar.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            ""
        }
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

}
