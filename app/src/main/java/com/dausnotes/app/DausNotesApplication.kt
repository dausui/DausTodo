package com.dausnotes.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.dausnotes.app.data.database.AppDatabase
import com.dausnotes.app.data.repository.NotesRepository
import com.dausnotes.app.data.repository.PomodoroRepository

class DausNotesApplication : Application() {
    
    companion object {
        const val POMODORO_CHANNEL_ID = "pomodoro_timer_channel"
        const val POMODORO_CHANNEL_NAME = "Pomodoro Timer"
        const val POMODORO_CHANNEL_DESCRIPTION = "Notifications for Pomodoro timer sessions"
    }
    
    // Database instance
    val database by lazy { 
        AppDatabase.getDatabase(this)
    }
    
    // Repositories
    val notesRepository by lazy { 
        NotesRepository(database.notesDao())
    }
    
    val pomodoroRepository by lazy { 
        PomodoroRepository(database.pomodoroDao())
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pomodoroChannel = NotificationChannel(
                POMODORO_CHANNEL_ID,
                POMODORO_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = POMODORO_CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(pomodoroChannel)
        }
    }
}