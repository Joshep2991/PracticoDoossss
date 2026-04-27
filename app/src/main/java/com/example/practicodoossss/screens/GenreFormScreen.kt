package com.example.practicodoossss.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practicodoossss.viewmodel.GenreOperationState
import com.example.practicodoossss.viewmodel.GenreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreFormScreen(navController: NavController, viewModel: GenreViewModel = viewModel()) {
    var nombre by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf(false) }
    val operationState by viewModel.operationState.collectAsState()

    LaunchedEffect(operationState) {
        if (operationState is GenreOperationState.Success) {
            navController.popBackStack()
            viewModel.resetOperationState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear genero") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; nombreError = false },
                label = { Text("Nombre del genero *") },
                isError = nombreError,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError) Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)

            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        viewModel.createGenre(nombre) {}
                    } else {
                        nombreError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is GenreOperationState.Loading
            ) {
                if (operationState is GenreOperationState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar genero")
                }
            }

            if (operationState is GenreOperationState.Error) {
                Text(
                    text = (operationState as GenreOperationState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}