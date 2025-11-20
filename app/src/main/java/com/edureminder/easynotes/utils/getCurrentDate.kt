package com.edureminder.easynotes.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getCurrentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(
        Calendar.getInstance().time
    ).toString()
}