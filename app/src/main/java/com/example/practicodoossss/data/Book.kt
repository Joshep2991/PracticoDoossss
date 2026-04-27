package com.example.practicodoossss.data

data class Book(
    val id: Int? = null,
    val nombre: String,
    val autor: String,
    val editorial: String,
    val imagen: String,
    val sinopsis: String,
    val isbn: String,
    val calificacion: Int
)