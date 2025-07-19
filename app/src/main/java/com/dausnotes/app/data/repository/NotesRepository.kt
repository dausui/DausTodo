package com.dausnotes.app.data.repository

import androidx.lifecycle.LiveData
import com.dausnotes.app.data.database.NotesDao
import com.dausnotes.app.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotesRepository(
    private val notesDao: NotesDao
) {
    
    fun getAllNotes(): LiveData<List<Note>> = notesDao.getAllNotes()
    
    fun getFavoriteNotes(): LiveData<List<Note>> = notesDao.getFavoriteNotes()
    
    fun getNotesByCategory(category: String): LiveData<List<Note>> = 
        notesDao.getNotesByCategory(category)
    
    fun searchNotes(query: String): LiveData<List<Note>> = notesDao.searchNotes(query)
    
    fun getAllCategories(): LiveData<List<String>> = notesDao.getAllCategories()
    
    fun getNotesCount(): LiveData<Int> = notesDao.getNotesCount()
    
    fun getFavoriteNotesCount(): LiveData<Int> = notesDao.getFavoriteNotesCount()
    
    fun getRecentNotes(limit: Int = 5): LiveData<List<Note>> = notesDao.getRecentNotes(limit)
    
    suspend fun getNoteById(id: Long): Note? = withContext(Dispatchers.IO) {
        notesDao.getNoteById(id)
    }
    
    suspend fun insertNote(note: Note): Long = withContext(Dispatchers.IO) {
        notesDao.insertNote(note)
    }
    
    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        val updatedNote = note.copy(updatedAt = System.currentTimeMillis())
        notesDao.updateNote(updatedNote)
    }
    
    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        notesDao.deleteNote(note)
    }
    
    suspend fun deleteNoteById(id: Long) = withContext(Dispatchers.IO) {
        notesDao.deleteNoteById(id)
    }
    
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        notesDao.updateFavoriteStatus(id, isFavorite)
    }
    
    suspend fun deleteAllNotes() = withContext(Dispatchers.IO) {
        notesDao.deleteAllNotes()
    }
    
    suspend fun createNote(title: String, content: String, category: String = Note.CATEGORY_GENERAL): Long {
        val note = Note(
            title = title.trim(),
            content = content.trim(),
            category = category,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return insertNote(note)
    }
}