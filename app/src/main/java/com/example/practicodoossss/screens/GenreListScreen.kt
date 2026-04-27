package com.example.practicodoossss.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practicodoossss.viewmodel.GenreViewModel
import com.example.practicodoossss.viewmodel.GenresUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreListScreen(navController: NavController, viewModel: GenreViewModel = viewModel()) {
    val genresState by viewModel.genresUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de generos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Button(onClick = { navController.navigate("crear_genero") }) {
                        Text("Nuevo genero")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (genresState) {
                is GenresUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is GenresUiState.Success -> {
                    val generos = (genresState as GenresUiState.Success).genres
                    if (generos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay generos registrados")
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(generos) { genero ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = genero.nombre, modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                    }
                }
                is GenresUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${(genresState as GenresUiState.Error).message}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadGenres() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}