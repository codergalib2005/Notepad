package com.edureminder.easynotes.ui.mode

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ModeViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val themePreference = ModeSharedPreference(context)
    private val _isDarkTheme = MutableStateFlow(themePreference.selectedTabIndex.value)
    val isDarkTheme: StateFlow<Int> = _isDarkTheme

    init {
        viewModelScope.launch {
            themePreference.selectedTabIndex.collect { themeIndex ->
                _isDarkTheme.value = themeIndex
            }
        }
    }

    fun updateTheme(isDark: Int) {
        viewModelScope.launch {
            themePreference.save(isDark)
        }
    }
}