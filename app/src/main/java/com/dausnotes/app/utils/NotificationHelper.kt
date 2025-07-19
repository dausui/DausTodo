package com.dausnotes.app.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dausnotes.app.DausNotesApplication
import com.dausnotes.app.MainActivity
import com.dausnotes.app.R
import com.dausnotes.app.data.model.PomodoroSession

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        const val TIMER_NOTIFICATION_ID = 1001
        const val COMPLETION_NOTIFICATION_ID = 1002
        
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
    }
    
    fun showTimerNotification(
        sessionType: PomodoroSession.SessionType,
        timeRemaining: String,
        isRunning: Boolean
    ) {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = when (sessionType) {
            PomodoroSession.SessionType.WORK -> "Focus Time"
            PomodoroSession.SessionType.SHORT_BREAK -> "Short Break"
            PomodoroSession.SessionType.LONG_BREAK -> "Long Break"
        }
        
        val content = if (isRunning) "Time remaining: $timeRemaining" else "Timer paused: $timeRemaining"
        
        val notification = NotificationCompat.Builder(context, DausNotesApplication.POMODORO_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        notificationManager.notify(TIMER_NOTIFICATION_ID, notification)
    }
    
    fun showCompletionNotification(sessionType: PomodoroSession.SessionType) {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val (title, message) = when (sessionType) {
            PomodoroSession.SessionType.WORK -> Pair(
                "Focus Session Complete!",
                "Great job! Time for a break."
            )
            PomodoroSession.SessionType.SHORT_BREAK -> Pair(
                "Break Over!",
                "Ready to get back to work?"
            )
            PomodoroSession.SessionType.LONG_BREAK -> Pair(
                "Long Break Complete!",
                "Refreshed and ready for the next session!"
            )
        }
        
        val notification = NotificationCompat.Builder(context, DausNotesApplication.POMODORO_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        notificationManager.notify(COMPLETION_NOTIFICATION_ID, notification)
    }
    
    fun cancelTimerNotification() {
        notificationManager.cancel(TIMER_NOTIFICATION_ID)
    }
    
    fun cancelCompletionNotification() {
        notificationManager.cancel(COMPLETION_NOTIFICATION_ID)
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}