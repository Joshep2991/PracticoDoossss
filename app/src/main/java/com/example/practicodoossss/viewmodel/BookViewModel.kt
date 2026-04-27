package com.example.practicodoossss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicodoossss.data.Book
import com.example.practicodoossss.data.Genero
import com.example.practicodoossss.data.LibroGeneroRequest
import com.example.practicodoossss.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BooksUiState {
    object Loading : BooksUiState()
    data class Success(val books: List<Book>) : BooksUiState()
    data class Error(val message: String) : BooksUiState()
}

sealed class BookDetailUiState {
    object Loading : BookDetailUiState()
    data class Success(val book: Book) : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
}

sealed class OperationUiState {
    object Idle : OperationUiState()
    object Loading : OperationUiState()
    data class Success(val message: String) : OperationUiState()
    data class Error(val message: String) : OperationUiState()
}

class BookViewModel : ViewModel() {
    private val api = RetrofitClient.api

    private val _booksUiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val booksUiState: StateFlow<BooksUiState> = _booksUiState.asStateFlow()

    private val _bookDetailUiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val bookDetailUiState: StateFlow<BookDetailUiState> = _bookDetailUiState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationUiState>(OperationUiState.Idle)
    val operationState: StateFlow<OperationUiState> = _operationState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _booksUiState.value = BooksUiState.Loading
            try {
                val books = api.listBooks()
                _booksUiState.value = BooksUiState.Success(books)
            } catch (e: Exception) {
                _booksUiState.value = BooksUiState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun loadBookById(bookId: Int) {
        viewModelScope.launch {
            _bookDetailUiState.value = BookDetailUiState.Loading
            try {
                val book = api.getBookById(bookId)
                _bookDetailUiState.value = BookDetailUiState.Success(book)
            } catch (e: Exception) {
                _bookDetailUiState.value = BookDetailUiState.Error("Error al cargar detalle: ${e.message}")
            }
        }
    }

    fun createBook(book: Book, genreIds: List<Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = OperationUiState.Loading
            try {
                val createdBook = api.createBook(book)
                // Asignar generos
                for (genreId in genreIds) {
                    val request = LibroGeneroRequest(createdBook.id!!, genreId)
                    api.assignGenreToBook(request)
                }
                _operationState.value = OperationUiState.Success("Libro creado con exito")
                loadBooks() // refrescar lista
                onSuccess()
            } catch (e: Exception) {
                _operationState.value = OperationUiState.Error("Error al crear: ${e.message}")
            }
        }
    }

    fun updateBook(bookId: Int, book: Book, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = OperationUiState.Loading
            try {
                api.updateBook(bookId, book)
                _operationState.value = OperationUiState.Success("Libro actualizado con exito")
                loadBooks()
                onSuccess()
            } catch (e: Exception) {
                _operationState.value = OperationUiState.Error("Error al actualizar: ${e.message}")
            }
        }
    }

    fun deleteBook(bookId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = OperationUiState.Loading
            try {
                api.deleteBook(bookId)
                _operationState.value = OperationUiState.Success("Libro eliminado con exito")
                loadBooks()
                onSuccess()
            } catch (e: Exception) {
                _operationState.value = OperationUiState.Error("Error al eliminar: ${e.message}")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationUiState.Idle
    }
}