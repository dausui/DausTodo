package com.dausnotes.app.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dausnotes.app.data.model.Note

@Dao
interface NotesDao {
    
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): LiveData<List<Note>>
    
    @Query("SELECT * FROM notes WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteNotes(): LiveData<List<Note>>
    
    @Query("SELECT * FROM notes WHERE category = :category ORDER BY updatedAt DESC")
    fun getNotesByCategory(category: String): LiveData<List<Note>>
    
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): LiveData<List<Note>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?
    
    @Query("SELECT DISTINCT category FROM notes ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>
    
    @Query("SELECT COUNT(*) FROM notes")
    fun getNotesCount(): LiveData<Int>
    
    @Query("SELECT COUNT(*) FROM notes WHERE isFavorite = 1")
    fun getFavoriteNotesCount(): LiveData<Int>
    
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentNotes(limit: Int = 5): LiveData<List<Note>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long
    
    @Update
    suspend fun updateNote(note: Note)
    
    @Delete
    suspend fun deleteNote(note: Note)
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
    
    @Query("UPDATE notes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
}