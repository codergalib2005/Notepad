package com.edureminder.notebook.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

inline fun <reified T> safeDecodeList(json: String?): List<T> {
    if (json.isNullOrBlank()) return emptyList()
    return try {
        Json.decodeFromString(json)
    } catch (e: SerializationException) {
        // optionally log e
        emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}
