package com.dausnotes.app.utils

import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

class TimeFormatterTest {

    @Test
    fun `formatTimer formats milliseconds correctly`() {
        // Test cases: milliseconds to MM:SS format
        assertEquals("25:00", TimeFormatter.formatTimer(25 * 60 * 1000L))
        assertEquals("05:30", TimeFormatter.formatTimer(5 * 60 * 1000L + 30 * 1000L))
        assertEquals("00:45", TimeFormatter.formatTimer(45 * 1000L))
        assertEquals("00:00", TimeFormatter.formatTimer(0L))
        assertEquals("01:23", TimeFormatter.formatTimer(83 * 1000L))
    }

    @Test
    fun `formatDuration formats minutes to human readable`() {
        assertEquals("5m", TimeFormatter.formatDuration(5))
        assertEquals("25m", TimeFormatter.formatDuration(25))
        assertEquals("1h", TimeFormatter.formatDuration(60))
        assertEquals("2h", TimeFormatter.formatDuration(120))
        assertEquals("1h 30m", TimeFormatter.formatDuration(90))
        assertEquals("2h 15m", TimeFormatter.formatDuration(135))
    }

    @Test
    fun `formatRelativeTime formats correctly`() {
        val now = System.currentTimeMillis()
        
        // Just now (less than 1 minute)
        assertEquals("Just now", TimeFormatter.formatRelativeTime(now - 30 * 1000L))
        
        // Minutes ago
        assertEquals("5m ago", TimeFormatter.formatRelativeTime(now - 5 * 60 * 1000L))
        assertEquals("30m ago", TimeFormatter.formatRelativeTime(now - 30 * 60 * 1000L))
        
        // Hours ago
        assertEquals("2h ago", TimeFormatter.formatRelativeTime(now - 2 * 60 * 60 * 1000L))
        assertEquals("12h ago", TimeFormatter.formatRelativeTime(now - 12 * 60 * 60 * 1000L))
        
        // Days ago
        assertEquals("2d ago", TimeFormatter.formatRelativeTime(now - 2 * 24 * 60 * 60 * 1000L))
        assertEquals("5d ago", TimeFormatter.formatRelativeTime(now - 5 * 24 * 60 * 60 * 1000L))
    }

    @Test
    fun `isToday returns correct boolean`() {
        val now = System.currentTimeMillis()
        
        // Current time should be today
        assertTrue(TimeFormatter.isToday(now))
        
        // Few hours ago should be today
        assertTrue(TimeFormatter.isToday(now - 3 * 60 * 60 * 1000L))
        
        // Yesterday should not be today
        assertFalse(TimeFormatter.isToday(now - 25 * 60 * 60 * 1000L))
        
        // Tomorrow should not be today (though this won't happen in practice)
        assertFalse(TimeFormatter.isToday(now + 25 * 60 * 60 * 1000L))
    }

    @Test
    fun `getStartOfDay returns correct timestamp`() {
        val now = System.currentTimeMillis()
        val startOfDay = TimeFormatter.getStartOfDay(now)
        
        // Start of day should be less than or equal to current time
        assertTrue(startOfDay <= now)
        
        // The difference should be less than 24 hours
        assertTrue(now - startOfDay < 24 * 60 * 60 * 1000L)
        
        // Two calls for the same day should return the same start time
        val anotherTimeToday = now - 3 * 60 * 60 * 1000L // 3 hours ago
        assertEquals(startOfDay, TimeFormatter.getStartOfDay(anotherTimeToday))
    }

    @Test
    fun `formatDate formats timestamp correctly`() {
        // Test with a known timestamp (Jan 1, 2024, 12:00 PM UTC)
        val timestamp = 1704110400000L // This is approximately Jan 1, 2024
        val formatted = TimeFormatter.formatDate(timestamp)
        
        // Should contain year and be in expected format
        assertTrue(formatted.contains("2024"))
        assertTrue(formatted.contains("Jan"))
    }

    @Test
    fun `formatDateTime includes both date and time`() {
        val timestamp = System.currentTimeMillis()
        val formatted = TimeFormatter.formatDateTime(timestamp)
        
        // Should contain both date elements and time (colon for HH:MM)
        assertTrue(formatted.contains(":"))
        assertTrue(formatted.length > 10) // Should be longer than just date
    }

    @Test
    fun `formatTime formats time correctly`() {
        // Test with known timestamp
        val timestamp = 1704110400000L // This should be around 12:00
        val formatted = TimeFormatter.formatTime(timestamp)
        
        // Should be in HH:MM format
        assertTrue(formatted.matches(Regex("\\d{2}:\\d{2}")))
        assertEquals(5, formatted.length) // HH:MM is 5 characters
    }
}