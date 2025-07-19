package com.dausnotes.app.ui.pomodoro

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dausnotes.app.DausNotesApplication
import com.dausnotes.app.data.model.PomodoroSession
import com.dausnotes.app.data.repository.PomodoroRepository
import com.dausnotes.app.utils.PreferencesManager
import kotlinx.coroutines.launch

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: PomodoroRepository = 
        (application as DausNotesApplication).pomodoroRepository
    private val preferencesManager = PreferencesManager(application)
    
    private var countDownTimer: CountDownTimer? = null
    private var currentSessionId: Long? = null
    
    private val _timeRemaining = MutableLiveData<Long>()
    private val _isRunning = MutableLiveData<Boolean>()
    private val _currentSessionType = MutableLiveData<PomodoroSession.SessionType>()
    private val _isSessionComplete = MutableLiveData<Boolean>()
    private val _linkedNoteId = MutableLiveData<Long?>()
    private val _error = MutableLiveData<String>()
    
    val timeRemaining: LiveData<Long> = _timeRemaining
    val isRunning: LiveData<Boolean> = _isRunning
    val currentSessionType: LiveData<PomodoroSession.SessionType> = _currentSessionType
    val isSessionComplete: LiveData<Boolean> = _isSessionComplete
    val linkedNoteId: LiveData<Long?> = _linkedNoteId
    val error: LiveData<String> = _error
    
    // Repository data
    val todayCompletedSessions: LiveData<Int> = repository.getTodayCompletedSessionsCount()
    val totalCompletedSessions: LiveData<Int> = repository.getTotalCompletedSessionsCount()
    val todayFocusTime: LiveData<Int?> = repository.getTodayFocusTime()
    val totalFocusTime: LiveData<Int?> = repository.getTotalFocusTime()
    val weeklyStats = repository.getWeeklyStats()
    val todaySessions = repository.getTodaySessions()
    
    init {
        _isRunning.value = false
        _currentSessionType.value = PomodoroSession.SessionType.WORK
        _isSessionComplete.value = false
        _linkedNoteId.value = null
        
        // Set initial time based on session type
        val initialDuration = preferencesManager.getDurationForSessionType(
            PomodoroSession.SessionType.WORK
        )
        _timeRemaining.value = initialDuration * 60 * 1000L // Convert to milliseconds
        
        // Check for active session
        checkForActiveSession()
    }
    
    private fun checkForActiveSession() {
        viewModelScope.launch {
            try {
                val activeSession = repository.getActiveSession()
                if (activeSession != null) {
                    currentSessionId = activeSession.id
                    _currentSessionType.value = activeSession.sessionType
                    _linkedNoteId.value = activeSession.noteId
                    
                    // Calculate remaining time
                    val elapsed = System.currentTimeMillis() - activeSession.startTime
                    val totalDuration = activeSession.duration * 60 * 1000L
                    val remaining = maxOf(0L, totalDuration - elapsed)
                    
                    if (remaining > 0) {
                        _timeRemaining.value = remaining
                        // Note: Timer is not automatically resumed to avoid unexpected behavior
                    } else {
                        // Session should have completed
                        completeCurrentSession()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error checking active session: ${e.message}"
            }
        }
    }
    
    fun startTimer(noteId: Long? = null) {
        if (_isRunning.value == true) return
        
        viewModelScope.launch {
            try {
                val sessionType = _currentSessionType.value ?: PomodoroSession.SessionType.WORK
                val duration = preferencesManager.getDurationForSessionType(sessionType)
                
                // Create new session if none exists
                if (currentSessionId == null) {
                    currentSessionId = repository.startSession(noteId, sessionType, duration)
                    _linkedNoteId.value = noteId
                    _timeRemaining.value = duration * 60 * 1000L
                }
                
                startCountdown()
                _isRunning.value = true
                _isSessionComplete.value = false
                
            } catch (e: Exception) {
                _error.value = "Error starting timer: ${e.message}"
            }
        }
    }
    
    fun pauseTimer() {
        stopCountdown()
        _isRunning.value = false
    }
    
    fun resumeTimer() {
        if (_timeRemaining.value ?: 0L > 0) {
            startCountdown()
            _isRunning.value = true
        }
    }
    
    fun stopTimer() {
        stopCountdown()
        _isRunning.value = false
        resetTimer()
    }
    
    fun resetTimer() {
        stopCountdown()
        currentSessionId = null
        _isRunning.value = false
        _isSessionComplete.value = false
        _linkedNoteId.value = null
        
        val sessionType = _currentSessionType.value ?: PomodoroSession.SessionType.WORK
        val duration = preferencesManager.getDurationForSessionType(sessionType)
        _timeRemaining.value = duration * 60 * 1000L
    }
    
    fun setSessionType(sessionType: PomodoroSession.SessionType) {
        if (_isRunning.value == true) return
        
        _currentSessionType.value = sessionType
        val duration = preferencesManager.getDurationForSessionType(sessionType)
        _timeRemaining.value = duration * 60 * 1000L
        _isSessionComplete.value = false
        currentSessionId = null
        _linkedNoteId.value = null
    }
    
    fun linkToNote(noteId: Long?) {
        _linkedNoteId.value = noteId
    }
    
    private fun startCountdown() {
        val timeLeft = _timeRemaining.value ?: return
        
        countDownTimer = object : CountDownTimer(timeLeft, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timeRemaining.value = millisUntilFinished
            }
            
            override fun onFinish() {
                completeCurrentSession()
            }
        }.start()
    }
    
    private fun stopCountdown() {
        countDownTimer?.cancel()
        countDownTimer = null
    }
    
    private fun completeCurrentSession() {
        stopCountdown()
        _isRunning.value = false
        _timeRemaining.value = 0L
        _isSessionComplete.value = true
        
        viewModelScope.launch {
            try {
                currentSessionId?.let { sessionId ->
                    repository.completeSession(sessionId)
                    preferencesManager.incrementTotalSessions()
                    preferencesManager.updateStreak()
                }
                
                // Auto-suggest next session type
                suggestNextSession()
                
            } catch (e: Exception) {
                _error.value = "Error completing session: ${e.message}"
            }
        }
    }
    
    private suspend fun suggestNextSession() {
        try {
            val nextSessionType = repository.getNextSessionType()
            _currentSessionType.value = nextSessionType
            
            val duration = repository.getRecommendedDuration(nextSessionType)
            _timeRemaining.value = duration * 60 * 1000L
            
            currentSessionId = null
            _linkedNoteId.value = null
            
        } catch (e: Exception) {
            _error.value = "Error suggesting next session: ${e.message}"
        }
    }
    
    fun acknowledgeCompletion() {
        _isSessionComplete.value = false
    }
    
    fun clearError() {
        _error.value = ""
    }
    
    override fun onCleared() {
        super.onCleared()
        stopCountdown()
    }
    
    // Helper methods for UI
    fun getSessionTypeDisplayName(sessionType: PomodoroSession.SessionType): String {
        return when (sessionType) {
            PomodoroSession.SessionType.WORK -> "Focus Session"
            PomodoroSession.SessionType.SHORT_BREAK -> "Short Break"
            PomodoroSession.SessionType.LONG_BREAK -> "Long Break"
        }
    }
    
    fun getSessionTypeColor(sessionType: PomodoroSession.SessionType): Int {
        return when (sessionType) {
            PomodoroSession.SessionType.WORK -> android.R.color.holo_red_light
            PomodoroSession.SessionType.SHORT_BREAK -> android.R.color.holo_green_light
            PomodoroSession.SessionType.LONG_BREAK -> android.R.color.holo_blue_light
        }
    }
}