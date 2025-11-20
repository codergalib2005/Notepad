package com.edureminder.easynotes.room.diary

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edureminder.easynotes.preferences.notes.SortOrder
import com.edureminder.easynotes.room.note.Status
import com.edureminder.easynotes.room.note.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository,
) : ViewModel() {
    // Mutable state internally
    private val _diaries = mutableStateOf<List<DiaryPreview>>(emptyList())
    val diaries: State<List<DiaryPreview>> = _diaries

    fun loadDiaries(
        search: String? = null,
        folderId: String? = null,
        mood: Int? = null,
        createdAfter: Long? = null,
        createdBefore: Long? = null,
        updatedAfter: Long? = null,
        updatedBefore: Long? = null,
        sortBy: String? = "createdDesc"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllDiaries(
                search, folderId, mood, createdAfter, createdBefore, updatedAfter, updatedBefore, sortBy
            )
            _diaries.value = list
        }
    }

    fun getOneDiary(id: String): Flow<Diary> {
        return repository.getOneDiary(id)
    }

    fun upsertDiary(diary: Diary) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertDiary(diary)
        }
    }

    fun deleteDiary(diary: Diary) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDiary(diary)
        }
    }

    fun updateFolderIdForDiaries(diaryIds: List<String>, newFolderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFolderIdForDiaries(diaryIds, newFolderId)
        }
    }

    fun updateStatusesForDiaries(diaryIds: List<String>, newStatus: Status) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStatusesForDiaries(diaryIds, newStatus)
        }
    }

    fun deleteDiaries(diaryIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDiaries(diaryIds)
        }
    }

    fun pinDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.pinDiary(diaryId)
        }
    }

    fun unpinDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unpinDiary(diaryId)
        }
    }
    fun lockDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.lockDiary(diaryId)
        }
    }

    fun unlockDiary(diaryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unlockDiary(diaryId)
        }
    }

    fun lockManyDiaries(diaryIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.lockManyDiaries(diaryIds)
        }
    }

    fun unlockManyDiaries(diariesIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unlockManyDiaries(diariesIds)
        }
    }

    fun getPendingSyncDiaryCount(): Flow<Int> {
        return repository.getPendingSyncDiaryCount()
    }

}