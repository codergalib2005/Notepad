package com.edureminder.notebook.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toSlashDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}
fun Long.toDashDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}
fun Long.toMonthDayYear(): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}
fun convertTo12HourFormat(time: String, is24Hour: Boolean?): String {
    // If is24Hour is true, keep it as is (no conversion)
    if (is24Hour == true) {
        return time
    }

    return try {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        outputFormat.format(date ?: return time)
    } catch (e: Exception) {
        time // fallback if parsing fails
    }
}