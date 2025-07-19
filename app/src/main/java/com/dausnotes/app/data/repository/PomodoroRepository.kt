package com.dausnotes.app.data.repository

import androidx.lifecycle.LiveData
import com.dausnotes.app.data.database.PomodoroDao
import com.dausnotes.app.data.model.PomodoroSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepository @Inject constructor(
    private val pomodoroDao: PomodoroDao
) {
    
    fun getAllSessions(): LiveData<List<PomodoroSession>> = pomodoroDao.getAllSessions()
    
    fun getSessionsByNote(noteId: Long): LiveData<List<PomodoroSession>> = 
        pomodoroDao.getSessionsByNote(noteId)
    
    fun getTodaySessions(): LiveData<List<PomodoroSession>> = pomodoroDao.getTodaySessions()
    
    fun getTodayCompletedSessionsCount(): LiveData<Int> = pomodoroDao.getTodayCompletedSessionsCount()
    
    fun getTotalCompletedSessionsCount(): LiveData<Int> = pomodoroDao.getTotalCompletedSessionsCount()
    
    fun getTodayFocusTime(): LiveData<Int?> = pomodoroDao.getTodayFocusTime()
    
    fun getTotalFocusTime(): LiveData<Int?> = pomodoroDao.getTotalFocusTime()
    
    fun getWeeklyStats(): LiveData<List<PomodoroDao.DailyStats>> = pomodoroDao.getWeeklyStats()
    
    suspend fun getSessionById(id: Long): PomodoroSession? = withContext(Dispatchers.IO) {
        pomodoroDao.getSessionById(id)
    }
    
    suspend fun getActiveSession(): PomodoroSession? = withContext(Dispatchers.IO) {
        pomodoroDao.getActiveSession()
    }
    
    suspend fun insertSession(session: PomodoroSession): Long = withContext(Dispatchers.IO) {
        pomodoroDao.insertSession(session)
    }
    
    suspend fun updateSession(session: PomodoroSession) = withContext(Dispatchers.IO) {
        pomodoroDao.updateSession(session)
    }
    
    suspend fun deleteSession(session: PomodoroSession) = withContext(Dispatchers.IO) {
        pomodoroDao.deleteSession(session)
    }
    
    suspend fun deleteSessionById(id: Long) = withContext(Dispatchers.IO) {
        pomodoroDao.deleteSessionById(id)
    }
    
    suspend fun completeSession(id: Long, endTime: Long = System.currentTimeMillis()) = withContext(Dispatchers.IO) {
        pomodoroDao.completeSession(id, true, endTime)
    }
    
    suspend fun deleteAllSessions() = withContext(Dispatchers.IO) {
        pomodoroDao.deleteAllSessions()
    }
    
    suspend fun startSession(
        noteId: Long? = null,
        sessionType: PomodoroSession.SessionType = PomodoroSession.SessionType.WORK,
        duration: Int = PomodoroSession.DEFAULT_WORK_DURATION
    ): Long {
        val session = PomodoroSession(
            noteId = noteId,
            duration = duration,
            sessionType = sessionType,
            startTime = System.currentTimeMillis()
        )
        return insertSession(session)
    }
    
    suspend fun getNextSessionType(): PomodoroSession.SessionType {
        val todayCompletedSessions = pomodoroDao.getTodayCompletedSessionsCount().value ?: 0
        
        return when {
            todayCompletedSessions == 0 -> PomodoroSession.SessionType.WORK
            todayCompletedSessions % (PomodoroSession.SESSIONS_BEFORE_LONG_BREAK * 2) == PomodoroSession.SESSIONS_BEFORE_LONG_BREAK -> {
                PomodoroSession.SessionType.LONG_BREAK
            }
            todayCompletedSessions % 2 == 1 -> PomodoroSession.SessionType.SHORT_BREAK
            else -> PomodoroSession.SessionType.WORK
        }
    }
    
    suspend fun getRecommendedDuration(sessionType: PomodoroSession.SessionType): Int {
        return when (sessionType) {
            PomodoroSession.SessionType.WORK -> PomodoroSession.DEFAULT_WORK_DURATION
            PomodoroSession.SessionType.SHORT_BREAK -> PomodoroSession.DEFAULT_SHORT_BREAK_DURATION
            PomodoroSession.SessionType.LONG_BREAK -> PomodoroSession.DEFAULT_LONG_BREAK_DURATION
        }
    }
}