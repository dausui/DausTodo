package com.dausnotes.app.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dausnotes.app.data.database.NotesDao
import com.dausnotes.app.data.model.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class NotesRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var notesDao: NotesDao

    private lateinit var repository: NotesRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = NotesRepository(notesDao)
    }

    @Test
    fun `getAllNotes returns LiveData from DAO`() {
        // Given
        val expectedNotes = MutableLiveData<List<Note>>()
        `when`(notesDao.getAllNotes()).thenReturn(expectedNotes)

        // When
        val result = repository.getAllNotes()

        // Then
        assert(result == expectedNotes)
        verify(notesDao).getAllNotes()
    }

    @Test
    fun `insertNote calls DAO insertNote`() = runTest {
        // Given
        val note = Note(
            id = 1,
            title = "Test Note",
            content = "Test Content",
            category = "Test"
        )
        `when`(notesDao.insertNote(note)).thenReturn(1L)

        // When
        val result = repository.insertNote(note)

        // Then
        assert(result == 1L)
        verify(notesDao).insertNote(note)
    }

    @Test
    fun `updateNote calls DAO updateNote with updated timestamp`() = runTest {
        // Given
        val originalNote = Note(
            id = 1,
            title = "Test Note",
            content = "Test Content",
            updatedAt = 1000L
        )

        // When
        repository.updateNote(originalNote)

        // Then
        verify(notesDao).updateNote(argThat { note ->
            note.id == originalNote.id &&
            note.title == originalNote.title &&
            note.content == originalNote.content &&
            note.updatedAt > originalNote.updatedAt
        })
    }

    @Test
    fun `deleteNote calls DAO deleteNote`() = runTest {
        // Given
        val note = Note(
            id = 1,
            title = "Test Note",
            content = "Test Content"
        )

        // When
        repository.deleteNote(note)

        // Then
        verify(notesDao).deleteNote(note)
    }

    @Test
    fun `createNote creates note with correct parameters`() = runTest {
        // Given
        val title = "Test Title"
        val content = "Test Content"
        val category = "Work"
        `when`(notesDao.insertNote(any())).thenReturn(1L)

        // When
        val result = repository.createNote(title, content, category)

        // Then
        assert(result == 1L)
        verify(notesDao).insertNote(argThat { note ->
            note.title == title.trim() &&
            note.content == content.trim() &&
            note.category == category
        })
    }

    @Test
    fun `toggleFavorite calls DAO updateFavoriteStatus`() = runTest {
        // Given
        val noteId = 1L
        val isFavorite = true

        // When
        repository.toggleFavorite(noteId, isFavorite)

        // Then
        verify(notesDao).updateFavoriteStatus(noteId, isFavorite)
    }
}