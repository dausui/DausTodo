package com.dausnotes.app.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.dausnotes.app.DausNotesApplication
import com.dausnotes.app.data.model.Note
import com.dausnotes.app.data.repository.NotesRepository
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NotesRepository = 
        (application as DausNotesApplication).notesRepository
    
    private val _searchQuery = MutableLiveData<String>()
    private val _selectedCategory = MutableLiveData<String>()
    private val _error = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()
    
    val searchQuery: LiveData<String> = _searchQuery
    val selectedCategory: LiveData<String> = _selectedCategory
    val error: LiveData<String> = _error
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Notes data based on search and filter
    val notes: LiveData<List<Note>> = _searchQuery.switchMap { query ->
        _selectedCategory.switchMap { category ->
            when {
                !query.isNullOrBlank() -> repository.searchNotes(query)
                !category.isNullOrBlank() && category != "All Categories" -> {
                    repository.getNotesByCategory(category)
                }
                else -> repository.getAllNotes()
            }
        }
    }
    
    val favoriteNotes: LiveData<List<Note>> = repository.getFavoriteNotes()
    val allCategories: LiveData<List<String>> = repository.getAllCategories()
    val notesCount: LiveData<Int> = repository.getNotesCount()
    val favoriteNotesCount: LiveData<Int> = repository.getFavoriteNotesCount()
    val recentNotes: LiveData<List<Note>> = repository.getRecentNotes(5)
    
    init {
        _searchQuery.value = ""
        _selectedCategory.value = "All Categories"
        _isLoading.value = false
    }
    
    fun searchNotes(query: String) {
        _searchQuery.value = query
    }
    
    fun filterByCategory(category: String) {
        _selectedCategory.value = category
        _searchQuery.value = "" // Clear search when filtering by category
    }
    
    fun createNote(title: String, content: String, category: String = Note.CATEGORY_GENERAL) {
        if (title.isBlank()) {
            _error.value = "Title cannot be empty"
            return
        }
        
        if (content.isBlank()) {
            _error.value = "Content cannot be empty"
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.createNote(title, content, category)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error creating note: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun updateNote(note: Note) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateNote(note)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error updating note: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteNote(note)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error deleting note: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun toggleFavorite(noteId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(noteId, isFavorite)
            } catch (e: Exception) {
                _error.value = "Error updating favorite: ${e.message}"
            }
        }
    }
    
    fun deleteAllNotes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteAllNotes()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error deleting all notes: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = ""
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    fun getCurrentSearchQuery(): String = _searchQuery.value ?: ""
    fun getCurrentCategory(): String = _selectedCategory.value ?: "All Categories"
}