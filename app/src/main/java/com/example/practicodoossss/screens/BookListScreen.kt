package com.example.practicodoossss.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.practicodoossss.data.Book
import com.example.practicodoossss.viewmodel.BooksUiState
import com.example.practicodoossss.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(navController: NavController, viewModel: BookViewModel = viewModel()) {
    val booksUiState by viewModel.booksUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca - Lista de libros") },
                actions = {
                    Button(onClick = { navController.navigate("crear_libro") }) {
                        Text("Nuevo libro")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { navController.navigate("lista_generos") }) {
                        Text("Generos")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (booksUiState) {
                is BooksUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is BooksUiState.Success -> {
                    val books = (booksUiState as BooksUiState.Success).books
                    if (books.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay libros disponibles")
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(books) { book ->
                                BookCard(book = book, onClick = {
                                    navController.navigate("detalle_libro/${book.id}")
                                })
                            }
                        }
                    }
                }
                is BooksUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${(booksUiState as BooksUiState.Error).message}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadBooks() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(book.imagen),
                contentDescription = "Portada",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(text = book.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Autor: ${book.autor}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}