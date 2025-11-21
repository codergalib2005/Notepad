package com.edureminder.notebook.ui.mode

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.content.edit

class ModeSharedPreference(private val context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("_mode_notepad_preferences", Context.MODE_PRIVATE)

    private val _selectedTab = MutableStateFlow(getSaved())
    val selectedTabIndex: StateFlow<Int> = _selectedTab


    fun save(tabId: Int) {
        preferences.edit {
            putString("_is_dark_theme", tabId.toString())
        }
        _selectedTab.value = tabId // Update the state flow value
    }

    fun getSaved(): Int {
        return preferences.getString("_is_dark_theme", 0.toString())?.toIntOrNull() ?: 0
    }
}