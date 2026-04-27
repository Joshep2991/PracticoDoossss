package com.example.practicodoossss.network

import com.example.practicodoossss.data.Book
import com.example.practicodoossss.data.Genero
import com.example.practicodoossss.data.LibroGeneroRequest
import retrofit2.http.*

interface ApiService {
    @GET("libros")
    suspend fun listBooks(): List<Book>

    @GET("libros/{id}")
    suspend fun getBookById(@Path("id") id: Int): Book

    @POST("libros")
    suspend fun createBook(@Body book: Book): Book

    @PUT("libros/{id}")
    suspend fun updateBook(@Path("id") id: Int, @Body book: Book): Book

    @DELETE("libros/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Unit

    @GET("generos")
    suspend fun listGenres(): List<Genero>

    @POST("generos")
    suspend fun createGenre(@Body genero: Genero): Genero

    // Asignar un genero a un libro
    @POST("libro-genero")
    suspend fun assignGenreToBook(@Body request: LibroGeneroRequest): Unit
}