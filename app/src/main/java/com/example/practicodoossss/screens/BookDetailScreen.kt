package com.example.practicodoossss.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.practicodoossss.viewmodel.BookDetailUiState
import com.example.practicodoossss.viewmodel.BookViewModel
import com.example.practicodoossss.viewmodel.OperationUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: Int,
    viewModel: BookViewModel = viewModel()
) {
    val detailState by viewModel.bookDetailUiState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationUiState.Success -> {
                navController.popBackStack()
                viewModel.resetOperationState()
            }
            is OperationUiState.Error -> {
                viewModel.resetOperationState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del libro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (detailState) {
                is BookDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is BookDetailUiState.Success -> {
                    val book = (detailState as BookDetailUiState.Success).book
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(book.imagen),
                            contentDescription = "Portada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(text = book.nombre, style = MaterialTheme.typography.headlineSmall)
                        Text(text = "Autor: ${book.autor}", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Editorial: ${book.editorial}")
                        Text(text = "ISBN: ${book.isbn}")
                        Text(text = "Calificacion: ${book.calificacion} / 5")
                        Text(text = "Sinopsis: ${book.sinopsis}", style = MaterialTheme.typography.bodyLarge)

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { navController.navigate("editar_libro/${book.id}") }) {
                                Text("Editar")
                            }
                            Button(onClick = { showDeleteDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
                is BookDetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${(detailState as BookDetailUiState.Error).message}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadBookById(bookId) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminacion") },
            text = { Text("¿Seguro que quieres eliminar este libro?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBook(bookId) {
                        showDeleteDialog = false
                    }
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}