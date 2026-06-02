package com.uth.gestionusuarios.models

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String?,
    val telefono: String?,
    val edad: Int,
    val fotoBase64: String? = null
)