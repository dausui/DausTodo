package com.dausnotes.app.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeFormatter {
    
    /**
     * Format milliseconds to MM:SS format
     */
    fun formatTimer(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
    
    /**
     * Format minutes to human readable duration
     */
    fun formatDuration(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes}m"
            minutes % 60 == 0 -> "${minutes / 60}h"
            else -> "${minutes / 60}h ${minutes % 60}m"
        }
    }
    
    /**
     * Format timestamp to relative time (e.g., "2 hours ago")
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "${minutes}m ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "${hours}h ago"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "${days}d ago"
            }
            else -> formatDate(timestamp)
        }
    }
    
    /**
     * Format timestamp to date string
     */
    fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to date and time string
     */
    fun formatDateTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to time only
     */
    fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date(timestamp))
    }
    
    /**
     * Check if timestamp is today
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val timestampCal = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return today.get(Calendar.YEAR) == timestampCal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == timestampCal.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}