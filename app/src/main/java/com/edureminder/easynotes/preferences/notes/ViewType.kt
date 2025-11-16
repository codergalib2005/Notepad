package com.edureminder.easynotes.preferences.notes

enum class ViewType {
    GRID, LIST;

    companion object {
        fun fromString(value: String): ViewType {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                LIST
            }
        }
    }
}