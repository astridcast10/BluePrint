package com.uth.gestionusuarios.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
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

class GuardarUsuarioActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEdad: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilEdad: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout

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

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) abrirCamara()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardar_usuario)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        val fabCamera = findViewById<FloatingActionButton>(R.id.fabCamera)
        val btnClean = findViewById<MaterialButton>(R.id.btnClean)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        imgAvatar  = findViewById(R.id.imgAvatar)
        etNombre   = findViewById(R.id.etNombre)
        etEdad     = findViewById(R.id.etEdad)
        etEmail    = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        tilNombre  = findViewById(R.id.tilNombre)
        tilEdad    = findViewById(R.id.tilEdad)
        tilEmail   = findViewById(R.id.tilEmail)
        tilTelefono = findViewById(R.id.tilTelefono)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Asterisco rojo en el hint
        val hint = SpannableString("Nombre completo *")
        hint.setSpan(
            ForegroundColorSpan(0xFFD32F2F.toInt()),
            16, 17,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tilNombre.hint = hint

        configurarFormatoTelefono()
        fabCamera.setOnClickListener { mostrarDialogoFoto() }
        btnClean.setOnClickListener { limpiarCampos() }
        
        btnSave.setOnClickListener {
            if (validarCampos()) {
                val nombre = etNombre.text.toString().trim()
                val edad = etEdad.text.toString().toIntOrNull() ?: 0
                
                var email = etEmail.text.toString().trim()
                if (email.isEmpty()) email = "N/A"
                
                var telefono = etTelefono.text.toString().trim()
                if (telefono.isEmpty()) telefono = "N/A"
                
                val fotoBase64 = imagenToBase64()

                val nuevoUsuario = Usuario(0, nombre, email, telefono, edad, fotoBase64)
                
                val db = UsuariosDB(this)
                val resultado = db.insertar(nuevoUsuario)

                if (resultado > 0) {
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarFormatoTelefono() {
        etTelefono.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                val text = s.toString().replace("-", "")
                if (text.length >= 4) {
                    isUpdating = true
                    val formatted = text.substring(0, 4) + "-" + text.substring(4, Math.min(text.length, 8))
                    etTelefono.setText(formatted)
                    etTelefono.setSelection(formatted.length)
                    isUpdating = false
                }
            }
        })
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
        if (email.isNotEmpty() && !email.contains("@")) {
            tilEmail.error = "Correo inválido (debe contener @)"
            esValido = false
        } else {
            tilEmail.error = null
        }

        val telefono = etTelefono.text.toString().trim()
        val regexTelefono = Regex("^\\d{4}-\\d{4}$")
        if (telefono.isNotEmpty() && !regexTelefono.matches(telefono)) {
            tilTelefono.error = "Formato inválido (####-####)"
            esValido = false
        } else {
            tilTelefono.error = null
        }

        return esValido
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

    private fun limpiarCampos() {
        etNombre.setText("")
        etEdad.setText("")
        etEmail.setText("")
        etTelefono.setText("")
        tilNombre.error = null
        tilEdad.error = null
        tilEmail.error = null
        tilTelefono.error = null
        imgAvatar.setImageResource(android.R.drawable.ic_menu_myplaces)
        imgAvatar.setPadding(10, 10, 10, 10)
        imgAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
    }
}
