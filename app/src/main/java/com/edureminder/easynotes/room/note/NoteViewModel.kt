package com.edureminder.easynotes.room.note

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edureminder.easynotes.preferences.notes.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository,
) : ViewModel() {

    fun getNotesByStatusAndSort(
        status: Status,
        sortOrder: SortOrder,
        selectedFolderId: String,
        filterByLock: Boolean = true,
        locked: Boolean = false,
        selectedType: Type? = null
    ): Flow<List<Note>> {
        return repository.getNotesByStatusAndSort(
            status,
            sortOrder,
            selectedFolderId,
            filterByLock,
            locked,
            selectedType
        )
    }

    fun getOneNote(id: String): Flow<Note> {
        return repository.getOneNote(id)
    }

    fun upsertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun updateFolderIdForNotes(noteIds: List<String>, newFolderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFolderIdForNotes(noteIds, newFolderId)
        }
    }

    fun updateStatusesForNotes(noteIds: List<String>, newStatus: Status) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStatusesForNotes(noteIds, newStatus)
        }
    }

    fun deleteNotes(noteIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNotes(noteIds)
        }
    }

    fun pinNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.pinNote(noteId)
        }
    }

    fun unpinNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unpinNote(noteId)
        }
    }
    fun lockNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.lockNote(noteId)
        }
    }

    fun unlockNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unlockNote(noteId)
        }
    }

    fun lockManyNotes(noteIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.lockManyNotes(noteIds)
        }
    }

    fun unlockManyNotes(noteIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unlockManyNotes(noteIds)
        }
    }

    fun getPendingSyncNoteCount(): Flow<Int> {
        return repository.getPendingSyncNoteCount()
    }

}