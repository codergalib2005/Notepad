package com.edureminder.easynotes.preferences.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edureminder.easynotes.room.note.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesSettingsViewModel @Inject constructor(
    private val preferencesManager: NotesPreferencesManager
) : ViewModel() {

    private val _viewType = MutableStateFlow(ViewType.LIST)
    val viewType: StateFlow<ViewType> = _viewType.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.UPDATED_AT_NEWEST_FIRST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _noteType = MutableStateFlow<Type?>(null)
    val noteType: StateFlow<Type?> = _noteType.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.viewTypeFlow.collectLatest { _viewType.value = it }
        }

        viewModelScope.launch {
            preferencesManager.sortOrderFlow.collectLatest { _sortOrder.value = it }
        }

        viewModelScope.launch {
            preferencesManager.noteTypeFlow.collectLatest { _noteType.value = it }
        }
    }

    fun setViewType(viewType: ViewType) {
        viewModelScope.launch {
            preferencesManager.setViewType(viewType)
        }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.setSortOrder(sortOrder)
        }
    }

    fun setNoteType(type: Type?) {
        viewModelScope.launch {
            preferencesManager.setNoteType(type)
        }
    }

    fun clearNoteType() {
        viewModelScope.launch {
            preferencesManager.clearNoteType()
        }
    }
}