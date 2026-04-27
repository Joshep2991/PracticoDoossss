package com.example.practicodoossss.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practicodoossss.data.Book
import com.example.practicodoossss.data.Genero
import com.example.practicodoossss.viewmodel.BookViewModel
import com.example.practicodoossss.viewmodel.GenreViewModel
import com.example.practicodoossss.viewmodel.GenresUiState
import com.example.practicodoossss.viewmodel.OperationUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreen(
    navController: NavController,
    bookId: Int? = null,
    bookViewModel: BookViewModel = viewModel(),
    genreViewModel: GenreViewModel = viewModel()
) {
    val isEdit = bookId != null
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var editorial by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var sinopsis by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("0") }

    var nombreError by remember { mutableStateOf(false) }
    var autorError by remember { mutableStateOf(false) }
    var editorialError by remember { mutableStateOf(false) }
    var imagenError by remember { mutableStateOf(false) }
    var sinopsisError by remember { mutableStateOf(false) }
    var isbnError by remember { mutableStateOf(false) }
    var calificacionError by remember { mutableStateOf(false) }

    val genresState by genreViewModel.genresUiState.collectAsState()
    var selectedGenres by remember { mutableStateOf(setOf<Genero>()) }
    val operationState by bookViewModel.operationState.collectAsState()

    LaunchedEffect(bookId) {
        if (isEdit && bookId != null) {
            bookViewModel.loadBookById(bookId)
            delay(300)
            val detailState = bookViewModel.bookDetailUiState.value
            if (detailState is com.example.practicodoossss.viewmodel.BookDetailUiState.Success) {
                val book = detailState.book
                nombre = book.nombre
                autor = book.autor
                editorial = book.editorial
                imagen = book.imagen
                sinopsis = book.sinopsis
                isbn = book.isbn
                calificacion = book.calificacion.toString()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isEdit) {
            genreViewModel.loadGenres()
        }
    }

    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationUiState.Success -> {
                navController.popBackStack()
                bookViewModel.resetOperationState()
            }
            else -> {}
        }
    }

    fun validarCampos(): Boolean {
        nombreError = nombre.isBlank()
        autorError = autor.isBlank()
        editorialError = editorial.isBlank()
        imagenError = imagen.isBlank() || !imagen.matches(Regex("^(http|https)://.*$"))
        sinopsisError = sinopsis.isBlank()
        isbnError = isbn.isBlank() || !isbn.matches(Regex("^[0-9]{13}$"))
        val calif = calificacion.toIntOrNull()
        calificacionError = calif == null || calif !in 0..5
        return !(nombreError || autorError || editorialError || imagenError || sinopsisError || isbnError || calificacionError)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Editar libro" else "Crear libro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; nombreError = false },
                label = { Text("Nombre *") },
                isError = nombreError,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError) Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = autor,
                onValueChange = { autor = it; autorError = false },
                label = { Text("Autor *") },
                isError = autorError,
                modifier = Modifier.fillMaxWidth()
            )
            if (autorError) Text("El autor es obligatorio", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = editorial,
                onValueChange = { editorial = it; editorialError = false },
                label = { Text("Editorial *") },
                isError = editorialError,
                modifier = Modifier.fillMaxWidth()
            )
            if (editorialError) Text("La editorial es obligatoria", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = imagen,
                onValueChange = { imagen = it; imagenError = false },
                label = { Text("URL de imagen * (debe iniciar con http:// o https://)") },
                isError = imagenError,
                modifier = Modifier.fillMaxWidth()
            )
            if (imagenError) Text("URL valida requerida", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = sinopsis,
                onValueChange = { sinopsis = it; sinopsisError = false },
                label = { Text("Sinopsis *") },
                isError = sinopsisError,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            if (sinopsisError) Text("La sinopsis es obligatoria", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it; isbnError = false },
                label = { Text("ISBN * (13 digitos numericos)") },
                isError = isbnError,
                modifier = Modifier.fillMaxWidth()
            )
            if (isbnError) Text("ISBN debe tener 13 digitos", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = calificacion,
                onValueChange = { calificacion = it; calificacionError = false },
                label = { Text("Calificacion (0 a 5) *") },
                isError = calificacionError,
                modifier = Modifier.fillMaxWidth()
            )
            if (calificacionError) Text("Calificacion debe ser numero entre 0 y 5", color = MaterialTheme.colorScheme.error)

            if (!isEdit) {
                Text("Generos (puedes seleccionar varios)", style = MaterialTheme.typography.titleMedium)
                when (genresState) {
                    is GenresUiState.Loading -> CircularProgressIndicator()
                    is GenresUiState.Success -> {
                        val generos = (genresState as GenresUiState.Success).genres
                        generos.forEach { genero ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedGenres.contains(genero),
                                    onCheckedChange = { isChecked ->
                                        selectedGenres = if (isChecked) {
                                            selectedGenres + genero
                                        } else {
                                            selectedGenres - genero
                                        }
                                    }
                                )
                                Text(genero.nombre)
                            }
                        }
                        if (generos.isEmpty()) Text("No hay generos registrados. Crea uno primero.")
                    }
                    is GenresUiState.Error -> Text("Error al cargar generos")
                }
                Button(onClick = { navController.navigate("crear_genero") }) {
                    Text("Crear nuevo genero")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validarCampos()) {
                        val califInt = calificacion.toInt()
                        val book = Book(
                            nombre = nombre,
                            autor = autor,
                            editorial = editorial,
                            imagen = imagen,
                            sinopsis = sinopsis,
                            isbn = isbn,
                            calificacion = califInt
                        )
                        if (isEdit && bookId != null) {
                            bookViewModel.updateBook(bookId, book) {}
                        } else {
                            val genreIds = selectedGenres.mapNotNull { it.id }
                            bookViewModel.createBook(book, genreIds) {}
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is OperationUiState.Loading
            ) {
                if (operationState is OperationUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isEdit) "Actualizar" else "Crear")
                }
            }

            if (operationState is OperationUiState.Error) {
                Text(
                    text = (operationState as OperationUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}