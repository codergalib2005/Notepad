package com.edureminder.easynotes.ui.theme

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
class ThemeViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val themePreference = ThemeSharedPreference(context)
    private val _isTheme = MutableStateFlow(themePreference.selectedTabIndex.value)
    val isTheme: StateFlow<Int> = _isTheme

    init {
        viewModelScope.launch {
            themePreference.selectedTabIndex.collect { themeIndex ->
                _isTheme.value = themeIndex
            }
        }
    }

    fun updateTheme(isThem: Int) {
        viewModelScope.launch {
            themePreference.save(isThem)
        }
    }
}