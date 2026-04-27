package com.example.practicodoossss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicodoossss.data.Genero
import com.example.practicodoossss.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GenresUiState {
    object Loading : GenresUiState()
    data class Success(val genres: List<Genero>) : GenresUiState()
    data class Error(val message: String) : GenresUiState()
}

sealed class GenreOperationState {
    object Idle : GenreOperationState()
    object Loading : GenreOperationState()
    data class Success(val message: String) : GenreOperationState()
    data class Error(val message: String) : GenreOperationState()
}

class GenreViewModel : ViewModel() {
    private val api = RetrofitClient.api

    private val _genresUiState = MutableStateFlow<GenresUiState>(GenresUiState.Loading)
    val genresUiState: StateFlow<GenresUiState> = _genresUiState.asStateFlow()

    private val _operationState = MutableStateFlow<GenreOperationState>(GenreOperationState.Idle)
    val operationState: StateFlow<GenreOperationState> = _operationState.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        viewModelScope.launch {
            _genresUiState.value = GenresUiState.Loading
            try {
                val genres = api.listGenres()
                _genresUiState.value = GenresUiState.Success(genres)
            } catch (e: Exception) {
                _genresUiState.value = GenresUiState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun createGenre(nombre: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = GenreOperationState.Loading
            try {
                val newGenre = Genero(nombre = nombre)
                api.createGenre(newGenre)
                _operationState.value = GenreOperationState.Success("Genero creado con exito")
                loadGenres() // refrescar lista
                onSuccess()
            } catch (e: Exception) {
                _operationState.value = GenreOperationState.Error("Error al crear genero: ${e.message}")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = GenreOperationState.Idle
    }
}