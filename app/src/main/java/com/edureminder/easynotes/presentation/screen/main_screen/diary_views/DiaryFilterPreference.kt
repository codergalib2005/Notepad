package com.edureminder.easynotes.presentation.screen.main_screen.diary_views

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class DiaryFilterPreference(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("diary_filters", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FOLDER = "folder"
        private const val KEY_MOOD = "mood"
        private const val KEY_SORT = "sort"
        private const val KEY_CREATED_AFTER = "createdAfter"
        private const val KEY_CREATED_BEFORE = "createdBefore"
        private const val KEY_UPDATED_AFTER = "updatedAfter"
        private const val KEY_UPDATED_BEFORE = "updatedBefore"
    }

    var folder: String?
        get() = prefs.getString(KEY_FOLDER, null)
        set(value) = prefs.edit { putString(KEY_FOLDER, value) }

    var mood: Int?
        get() = prefs.getInt(KEY_MOOD, -1).let { if (it == -1) null else it }
        set(value) = prefs.edit { putInt(KEY_MOOD, value ?: -1) }

    var sortOption: String?
        get() = prefs.getString(KEY_SORT, "createdDesc") // "createdDesc" = last created first
        set(value) = prefs.edit { putString(KEY_SORT, value) }

    var createdAfter: Long?
        get() = prefs.getLong(KEY_CREATED_AFTER, -1L).let { if (it == -1L) null else it }
        set(value) = prefs.edit { putLong(KEY_CREATED_AFTER, value ?: -1L) }

    var createdBefore: Long?
        get() = prefs.getLong(KEY_CREATED_BEFORE, -1L).let { if (it == -1L) null else it }
        set(value) = prefs.edit { putLong(KEY_CREATED_BEFORE, value ?: -1L) }

    var updatedAfter: Long?
        get() = prefs.getLong(KEY_UPDATED_AFTER, -1L).let { if (it == -1L) null else it }
        set(value) = prefs.edit { putLong(KEY_UPDATED_AFTER, value ?: -1L) }

    var updatedBefore: Long?
        get() = prefs.getLong(KEY_UPDATED_BEFORE, -1L).let { if (it == -1L) null else it }
        set(value) = prefs.edit { putLong(KEY_UPDATED_BEFORE, value ?: -1L) }

    fun clearAll() {
        prefs.edit { clear() }
    }
}
