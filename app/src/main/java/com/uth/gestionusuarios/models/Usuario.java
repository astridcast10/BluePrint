package com.uth.gestionusuarios.models;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private int edad;
    private String fotoBase64;
    private int status;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String correo, String telefono, int edad, String fotoBase64) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.edad = edad;
        this.fotoBase64 = fotoBase64;
        this.status = 1;
    }

    public Usuario(int id, String nombre, String correo, String telefono, int edad, String fotoBase64, int status) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.edad = edad;
        this.fotoBase64 = fotoBase64;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
