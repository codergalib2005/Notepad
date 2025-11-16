package com.edureminder.easynotes.utils

import java.util.UUID

fun generateUniqueId(): String {
    val timestamp = System.currentTimeMillis()
    val randomUUID = UUID.randomUUID().toString().take(8)  // short UUID
    return "fld_${timestamp}_$randomUUID"
}