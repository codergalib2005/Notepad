package com.edureminder.notebook.room.folder

import com.edureminder.notebook.room.note.SyncStatus
import kotlinx.coroutines.flow.Flow

class FolderRepository(private val folderDao: FolderDao) {

    suspend fun getFolderCount(): Int = folderDao.getFolderCount()

    suspend fun getMaxPosition(): Int = folderDao.getMaxPosition() ?: 0

    fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()

    suspend fun upsertFolder(folder: Folder) = folderDao.upsertFolder(folder)

    suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder)

    suspend fun getFolderById(id: String): Folder? = folderDao.getFolderById(id)

    suspend fun renameFolder(id: String, newName: String) =
        folderDao.renameFolder(id, newName, System.currentTimeMillis())

    suspend fun updateFolderColor(id: String, newColor: String) =
        folderDao.updateFolderColor(id, newColor, System.currentTimeMillis())

    suspend fun updateNoteCount(id: String, newCount: Int) =
        folderDao.updateNoteCount(id, newCount)

    suspend fun updateFolders(folders: List<Folder>) =
        folderDao.updateFolders(folders)

    suspend fun getPendingSyncFolders(): List<Folder> {
        return folderDao.getFoldersBySyncStatus(SyncStatus.PENDING.name)
    }

    suspend fun markAllFoldersAsSynced(timestamp: Long) {
        folderDao.markAllFoldersAsSynced(timestamp)
    }

    suspend fun updateFolderSyncTimestamp(folderId: String, timestamp: Long) {
        folderDao.updateFolderSyncTimestamp(folderId, timestamp)
    }

    /**
     * Smart restore logic: updates existing folders while preserving position and merging count.
     */
    suspend fun restoreFoldersSmartly(foldersFromBackup: List<Folder>) {
        val currentFolders = folderDao.fetchAllFolders()
        val currentMap = currentFolders.associateBy { it.id }

        val foldersToUpdate = mutableListOf<Folder>()
        val foldersToInsert = mutableListOf<Folder>()

        for (backupFolder in foldersFromBackup) {
            val existingFolder = currentMap[backupFolder.id]

            if (existingFolder != null) {
                val updated = if (backupFolder.updatedAt > existingFolder.updatedAt) {
                    // Remote (backup) folder is newer, preserve local position
                    backupFolder.copy(
                        position = existingFolder.position,
                        count = maxOf(backupFolder.count, existingFolder.count),
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    // Keep local folder unchanged
                    existingFolder
                }
                foldersToUpdate.add(updated)
            } else {
                foldersToInsert.add(backupFolder)
            }
        }

        if (foldersToUpdate.isNotEmpty()) folderDao.updateFolders(foldersToUpdate)
        if (foldersToInsert.isNotEmpty()) folderDao.insertAll(foldersToInsert)
    }

}