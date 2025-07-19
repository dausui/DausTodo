package com.dausnotes.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.dausnotes.app.DausNotesApplication
import com.dausnotes.app.data.model.Note
import com.dausnotes.app.data.repository.NotesRepository
import com.dausnotes.app.data.repository.PomodoroRepository
import com.dausnotes.app.utils.PreferencesManager

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val notesRepository: NotesRepository = 
        (application as DausNotesApplication).notesRepository
    private val pomodoroRepository: PomodoroRepository = 
        application.pomodoroRepository
    private val preferencesManager = PreferencesManager(application)
    
    // Notes data
    val totalNotes: LiveData<Int> = notesRepository.getNotesCount()
    val favoriteNotes: LiveData<Int> = notesRepository.getFavoriteNotesCount()
    val recentNotes: LiveData<List<Note>> = notesRepository.getRecentNotes(5)
    
    // Pomodoro data
    val todayCompletedSessions: LiveData<Int> = pomodoroRepository.getTodayCompletedSessionsCount()
    val todayFocusTime: LiveData<Int?> = pomodoroRepository.getTodayFocusTime()
    val totalCompletedSessions: LiveData<Int> = pomodoroRepository.getTotalCompletedSessionsCount()
    val totalFocusTime: LiveData<Int?> = pomodoroRepository.getTotalFocusTime()
    
    // Preferences data
    private val _currentStreak = MutableLiveData<Int>()
    private val _bestStreak = MutableLiveData<Int>()
    
    val currentStreak: LiveData<Int> = _currentStreak
    val bestStreak: LiveData<Int> = _bestStreak
    
    // Combined dashboard stats
    val dashboardStats = MediatorLiveData<DashboardStats>().apply {
        var notesCount = 0
        var favoritesCount = 0
        var sessionsToday = 0
        var focusTimeToday = 0
        var streak = 0
        
        fun update() {
            value = DashboardStats(
                totalNotes = notesCount,
                favoriteNotes = favoritesCount,
                sessionsToday = sessionsToday,
                focusTimeToday = focusTimeToday,
                currentStreak = streak
            )
        }
        
        addSource(totalNotes) { 
            notesCount = it ?: 0
            update()
        }
        
        addSource(favoriteNotes) { 
            favoritesCount = it ?: 0
            update()
        }
        
        addSource(todayCompletedSessions) { 
            sessionsToday = it ?: 0
            update()
        }
        
        addSource(todayFocusTime) { 
            focusTimeToday = it ?: 0
            update()
        }
        
        addSource(currentStreak) { 
            streak = it ?: 0
            update()
        }
    }
    
    init {
        loadPreferencesData()
    }
    
    private fun loadPreferencesData() {
        _currentStreak.value = preferencesManager.currentStreak
        _bestStreak.value = preferencesManager.bestStreak
    }
    
    fun refreshData() {
        loadPreferencesData()
    }
    
    data class DashboardStats(
        val totalNotes: Int,
        val favoriteNotes: Int,
        val sessionsToday: Int,
        val focusTimeToday: Int, // in minutes
        val currentStreak: Int
    )
}