package com.jayanth.jcricket.ui.views.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatStartDate(timestamp: Long): String {
    if (timestamp <= 0) return ""
    return try {
        val date = Date(timestamp)
        val format = SimpleDateFormat("EEE, MMM d, hh:mm a", Locale.getDefault())
        format.format(date)
    } catch (e: Exception) {
        ""
    }
}

fun getRelativeTime(timestamp: Long): String {
    if (timestamp <= 0) return "-"
    val seconds = ((System.currentTimeMillis() - timestamp) / 1000).toInt()
    return when {
        seconds < 0 -> "Just now"
        seconds < 60 -> "${seconds}s ago"
        seconds < 3600 -> "${seconds / 60}m ago"
        seconds < 86400 -> "${seconds / 3600}h ago"
        else -> "${seconds / 86400}d ago"
    }
}
