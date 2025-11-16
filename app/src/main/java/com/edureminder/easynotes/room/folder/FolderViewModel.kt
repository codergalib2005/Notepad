package com.edureminder.easynotes.room.folder

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val repository: FolderRepository
) : ViewModel() {

    val allFolders: Flow<List<Folder>> = repository.getAllFolders()

    fun insertDummyDataIfEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getFolderCount() == 0) {
                val dummyFolders = listOf(
                    Folder(name = "Personal", color = "#FFCDD2", position = 1),
                    Folder(name = "Work", color = "#BBDEFB", position = 2),
                    Folder(name = "Ideas", color = "#C8E6C9", position = 3)
                )
                dummyFolders.forEach { repository.upsertFolder(it) }
            }
        }
    }

    fun createNewFolder(name: String, color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val nextPosition = repository.getMaxPosition() + 1
            val newFolder = Folder(name = name, color = color, position = nextPosition)
            repository.upsertFolder(newFolder)
        }
    }

    fun updateFolder(folderId: String, newName: String, newColor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getFolderById(folderId)
            if (current != null) {
                if (current.name != newName) {
                    repository.renameFolder(folderId, newName)
                }
                if (current.color != newColor) {
                    repository.updateFolderColor(folderId, newColor)
                }
            }
        }
    }

    fun upsertFolder(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            // Check if the folder exists by its ID
            val existingFolder = repository.getFolderById(folder.id)

            // If the folder exists, keep the same position
            if (existingFolder != null) {
                // If the folder exists, no need to update the position, just upsert the folder as is
                val folderToUpsert = folder.copy(position = existingFolder.position)
                repository.upsertFolder(folderToUpsert)
            } else {
                // If the folder is new, calculate the next position
                val maxPosition = repository.getMaxPosition()
                val nextPosition = maxPosition + 1

                // Prepare the folder with the next unique position
                val folderToUpsert = folder.copy(position = nextPosition)

                // Perform upsert operation
                repository.upsertFolder(folderToUpsert)
            }
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFolder(folder)
        }
    }

    fun updateNoteCount(id: String, newCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNoteCount(id, newCount)
        }
    }

    fun updateFolderPositions(folders: List<Folder>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFolders(folders)
        }
    }
    fun reorderFolders(updatedOrder: List<Folder>) {
        viewModelScope.launch(Dispatchers.IO) {
            val reordered = updatedOrder.mapIndexed { index, folder ->
                folder.copy(position = index + 1)
            }
            repository.updateFolders(reordered)
        }
    }

}