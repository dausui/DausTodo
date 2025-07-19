package com.dausnotes.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.dausnotes.app.data.model.PomodoroSession

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "dausnotes_preferences"
        
        // Timer preferences
        private const val KEY_WORK_DURATION = "work_duration"
        private const val KEY_SHORT_BREAK_DURATION = "short_break_duration"
        private const val KEY_LONG_BREAK_DURATION = "long_break_duration"
        private const val KEY_AUTO_START_BREAKS = "auto_start_breaks"
        private const val KEY_AUTO_START_WORK = "auto_start_work"
        
        // Sound preferences
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_ALARM_TONE = "alarm_tone"
        
        // UI preferences
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_THEME_COLOR = "theme_color"
        
        // Statistics
        private const val KEY_TOTAL_SESSIONS = "total_sessions"
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_BEST_STREAK = "best_streak"
        private const val KEY_LAST_SESSION_DATE = "last_session_date"
    }
    
    // Timer Duration Settings
    var workDuration: Int
        get() = prefs.getInt(KEY_WORK_DURATION, PomodoroSession.DEFAULT_WORK_DURATION)
        set(value) = prefs.edit().putInt(KEY_WORK_DURATION, value).apply()
    
    var shortBreakDuration: Int
        get() = prefs.getInt(KEY_SHORT_BREAK_DURATION, PomodoroSession.DEFAULT_SHORT_BREAK_DURATION)
        set(value) = prefs.edit().putInt(KEY_SHORT_BREAK_DURATION, value).apply()
    
    var longBreakDuration: Int
        get() = prefs.getInt(KEY_LONG_BREAK_DURATION, PomodoroSession.DEFAULT_LONG_BREAK_DURATION)
        set(value) = prefs.edit().putInt(KEY_LONG_BREAK_DURATION, value).apply()
    
    var autoStartBreaks: Boolean
        get() = prefs.getBoolean(KEY_AUTO_START_BREAKS, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_START_BREAKS, value).apply()
    
    var autoStartWork: Boolean
        get() = prefs.getBoolean(KEY_AUTO_START_WORK, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_START_WORK, value).apply()
    
    // Sound Settings
    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()
    
    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()
    
    var alarmTone: String
        get() = prefs.getString(KEY_ALARM_TONE, "default") ?: "default"
        set(value) = prefs.edit().putString(KEY_ALARM_TONE, value).apply()
    
    // UI Settings
    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
    
    var themeColor: String
        get() = prefs.getString(KEY_THEME_COLOR, "blue") ?: "blue"
        set(value) = prefs.edit().putString(KEY_THEME_COLOR, value).apply()
    
    // Statistics
    var totalSessions: Int
        get() = prefs.getInt(KEY_TOTAL_SESSIONS, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_SESSIONS, value).apply()
    
    var currentStreak: Int
        get() = prefs.getInt(KEY_CURRENT_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_CURRENT_STREAK, value).apply()
    
    var bestStreak: Int
        get() = prefs.getInt(KEY_BEST_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_STREAK, value).apply()
    
    var lastSessionDate: Long
        get() = prefs.getLong(KEY_LAST_SESSION_DATE, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_SESSION_DATE, value).apply()
    
    // Helper methods
    fun getDurationForSessionType(sessionType: PomodoroSession.SessionType): Int {
        return when (sessionType) {
            PomodoroSession.SessionType.WORK -> workDuration
            PomodoroSession.SessionType.SHORT_BREAK -> shortBreakDuration
            PomodoroSession.SessionType.LONG_BREAK -> longBreakDuration
        }
    }
    
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }
    
    fun incrementTotalSessions() {
        totalSessions += 1
    }
    
    fun updateStreak() {
        val today = TimeFormatter.getStartOfDay()
        val lastSessionDay = TimeFormatter.getStartOfDay(lastSessionDate)
        
        when {
            lastSessionDate == 0L -> {
                // First session
                currentStreak = 1
            }
            today == lastSessionDay -> {
                // Same day, don't increment streak
                return
            }
            today - lastSessionDay == 86400000L -> {
                // Next day, increment streak
                currentStreak += 1
            }
            else -> {
                // Gap in days, reset streak
                currentStreak = 1
            }
        }
        
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }
        
        lastSessionDate = System.currentTimeMillis()
    }
}