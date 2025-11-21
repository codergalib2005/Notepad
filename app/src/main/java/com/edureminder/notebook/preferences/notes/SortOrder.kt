package com.edureminder.notebook.preferences.notes

enum class SortOrder {
    TITLE_ASCENDING,
    TITLE_DESCENDING,
    DATE_OLDEST_FIRST,
    DATE_NEWEST_FIRST,
    UPDATED_AT_OLDEST_FIRST,
    UPDATED_AT_NEWEST_FIRST;

    companion object {
        fun fromString(value: String): SortOrder {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                DATE_NEWEST_FIRST
            }
        }
    }
}