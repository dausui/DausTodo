package com.dausnotes.app.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dausnotes.app.data.model.Note
import com.dausnotes.app.data.model.PomodoroSession
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var notesDao: NotesDao
    private lateinit var pomodoroDao: PomodoroDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        notesDao = database.notesDao()
        pomodoroDao = database.pomodoroDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetNote() = runBlocking {
        // Given
        val note = Note(
            title = "Test Note",
            content = "Test Content",
            category = "Test"
        )

        // When
        val noteId = notesDao.insertNote(note)
        val retrievedNote = notesDao.getNoteById(noteId)

        // Then
        assertNotNull(retrievedNote)
        assertEquals(note.title, retrievedNote?.title)
        assertEquals(note.content, retrievedNote?.content)
        assertEquals(note.category, retrievedNote?.category)
    }

    @Test
    fun updateNote() = runBlocking {
        // Given
        val note = Note(
            title = "Original Title",
            content = "Original Content"
        )
        val noteId = notesDao.insertNote(note)
        val originalNote = notesDao.getNoteById(noteId)!!

        // When
        val updatedNote = originalNote.copy(
            title = "Updated Title",
            content = "Updated Content"
        )
        notesDao.updateNote(updatedNote)
        val retrievedNote = notesDao.getNoteById(noteId)

        // Then
        assertNotNull(retrievedNote)
        assertEquals("Updated Title", retrievedNote?.title)
        assertEquals("Updated Content", retrievedNote?.content)
    }

    @Test
    fun deleteNote() = runBlocking {
        // Given
        val note = Note(
            title = "Test Note",
            content = "Test Content"
        )
        val noteId = notesDao.insertNote(note)
        val insertedNote = notesDao.getNoteById(noteId)!!

        // When
        notesDao.deleteNote(insertedNote)
        val deletedNote = notesDao.getNoteById(noteId)

        // Then
        assertNull(deletedNote)
    }

    @Test
    fun insertAndGetPomodoroSession() = runBlocking {
        // Given
        val session = PomodoroSession(
            duration = 25,
            sessionType = PomodoroSession.SessionType.WORK,
            startTime = System.currentTimeMillis()
        )

        // When
        val sessionId = pomodoroDao.insertSession(session)
        val retrievedSession = pomodoroDao.getSessionById(sessionId)

        // Then
        assertNotNull(retrievedSession)
        assertEquals(session.duration, retrievedSession?.duration)
        assertEquals(session.sessionType, retrievedSession?.sessionType)
        assertEquals(session.startTime, retrievedSession?.startTime)
    }

    @Test
    fun completeSession() = runBlocking {
        // Given
        val session = PomodoroSession(
            duration = 25,
            sessionType = PomodoroSession.SessionType.WORK,
            startTime = System.currentTimeMillis(),
            isCompleted = false
        )
        val sessionId = pomodoroDao.insertSession(session)
        val endTime = System.currentTimeMillis()

        // When
        pomodoroDao.completeSession(sessionId, true, endTime)
        val completedSession = pomodoroDao.getSessionById(sessionId)

        // Then
        assertNotNull(completedSession)
        assertTrue(completedSession?.isCompleted ?: false)
        assertEquals(endTime, completedSession?.endTime)
    }

    @Test
    fun searchNotes() = runBlocking {
        // Given
        val notes = listOf(
            Note(title = "Kotlin Programming", content = "Learning Kotlin"),
            Note(title = "Android Development", content = "Building Android apps"),
            Note(title = "Database Design", content = "Room database tutorial")
        )
        
        notes.forEach { notesDao.insertNote(it) }

        // When
        val searchResults = notesDao.searchNotes("Android").getValueBlocking()

        // Then
        assertEquals(1, searchResults.size)
        assertEquals("Android Development", searchResults.first().title)
    }

    @Test
    fun getNotesByCategory() = runBlocking {
        // Given
        val notes = listOf(
            Note(title = "Work Note 1", content = "Content", category = "Work"),
            Note(title = "Personal Note", content = "Content", category = "Personal"),
            Note(title = "Work Note 2", content = "Content", category = "Work")
        )
        
        notes.forEach { notesDao.insertNote(it) }

        // When
        val workNotes = notesDao.getNotesByCategory("Work").getValueBlocking()

        // Then
        assertEquals(2, workNotes.size)
        assertTrue(workNotes.all { it.category == "Work" })
    }

    @Test
    fun toggleFavoriteNote() = runBlocking {
        // Given
        val note = Note(
            title = "Test Note",
            content = "Test Content",
            isFavorite = false
        )
        val noteId = notesDao.insertNote(note)

        // When
        notesDao.updateFavoriteStatus(noteId, true)
        val updatedNote = notesDao.getNoteById(noteId)

        // Then
        assertNotNull(updatedNote)
        assertTrue(updatedNote?.isFavorite ?: false)
    }
}

// Extension function to get LiveData value in tests
fun <T> androidx.lifecycle.LiveData<T>.getValueBlocking(): T {
    var value: T? = null
    val latch = java.util.concurrent.CountDownLatch(1)
    
    val observer = androidx.lifecycle.Observer<T> {
        value = it
        latch.countDown()
    }
    
    this.observeForever(observer)
    latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
    this.removeObserver(observer)
    
    return value!!
}