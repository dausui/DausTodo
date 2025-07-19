package com.dausnotes.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pomodoro_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long? = null,
    val duration: Int = 25, // minutes
    val isCompleted: Boolean = false,
    val sessionType: SessionType = SessionType.WORK,
    val startTime: Long,
    val endTime: Long? = null
) {
    enum class SessionType {
        WORK,
        SHORT_BREAK,
        LONG_BREAK
    }
    
    companion object {
        const val DEFAULT_WORK_DURATION = 25
        const val DEFAULT_SHORT_BREAK_DURATION = 5
        const val DEFAULT_LONG_BREAK_DURATION = 15
        const val SESSIONS_BEFORE_LONG_BREAK = 4
    }
}