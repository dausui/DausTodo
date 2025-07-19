package com.dausnotes.app.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dausnotes.app.data.model.PomodoroSession

@Dao
interface PomodoroDao {
    
    @Query("SELECT * FROM pomodoro_sessions ORDER BY startTime DESC")
    fun getAllSessions(): LiveData<List<PomodoroSession>>
    
    @Query("SELECT * FROM pomodoro_sessions WHERE noteId = :noteId ORDER BY startTime DESC")
    fun getSessionsByNote(noteId: Long): LiveData<List<PomodoroSession>>
    
    @Query("SELECT * FROM pomodoro_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): PomodoroSession?
    
    @Query("SELECT * FROM pomodoro_sessions WHERE DATE(startTime/1000, 'unixepoch') = DATE('now') ORDER BY startTime DESC")
    fun getTodaySessions(): LiveData<List<PomodoroSession>>
    
    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE isCompleted = 1 AND DATE(startTime/1000, 'unixepoch') = DATE('now')")
    fun getTodayCompletedSessionsCount(): LiveData<Int>
    
    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE isCompleted = 1")
    fun getTotalCompletedSessionsCount(): LiveData<Int>
    
    @Query("SELECT SUM(duration) FROM pomodoro_sessions WHERE isCompleted = 1 AND DATE(startTime/1000, 'unixepoch') = DATE('now')")
    fun getTodayFocusTime(): LiveData<Int?>
    
    @Query("SELECT SUM(duration) FROM pomodoro_sessions WHERE isCompleted = 1")
    fun getTotalFocusTime(): LiveData<Int?>
    
    @Query("""
        SELECT DATE(startTime/1000, 'unixepoch') as date, COUNT(*) as count
        FROM pomodoro_sessions 
        WHERE isCompleted = 1 
        AND DATE(startTime/1000, 'unixepoch') >= DATE('now', '-7 days')
        GROUP BY DATE(startTime/1000, 'unixepoch')
        ORDER BY date DESC
    """)
    fun getWeeklyStats(): LiveData<List<DailyStats>>
    
    @Query("SELECT * FROM pomodoro_sessions WHERE isCompleted = 0 ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveSession(): PomodoroSession?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSession): Long
    
    @Update
    suspend fun updateSession(session: PomodoroSession)
    
    @Delete
    suspend fun deleteSession(session: PomodoroSession)
    
    @Query("DELETE FROM pomodoro_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)
    
    @Query("UPDATE pomodoro_sessions SET isCompleted = :isCompleted, endTime = :endTime WHERE id = :id")
    suspend fun completeSession(id: Long, isCompleted: Boolean, endTime: Long)
    
    @Query("DELETE FROM pomodoro_sessions")
    suspend fun deleteAllSessions()
    
    data class DailyStats(
        val date: String,
        val count: Int
    )
}