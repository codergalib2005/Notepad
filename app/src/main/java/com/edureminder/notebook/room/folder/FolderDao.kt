package com.edureminder.notebook.room.folder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT COUNT(*) FROM folder")
    suspend fun getFolderCount(): Int

    @Query("SELECT MAX(position) FROM folder")
    suspend fun getMaxPosition(): Int?

    @Upsert
    suspend fun upsertFolder(folder: Folder)

    /**
     * new for sync
     *
     */
    @Upsert
    suspend fun upsertFolders(folders: List<Folder>)

    @Query("SELECT * FROM folder WHERE status = 'ACTIVE' ORDER BY position ASC")
    fun getAllActiveFoldersFlow(): Flow<List<Folder>>

    @Query("SELECT * FROM folder")
    suspend fun fetchAllFolders(): List<Folder>

    @Query("UPDATE folder SET status = :status, updatedAt = :timestamp, syncStatus = 'PENDING' WHERE id = :folderId")
    suspend fun updateFolderStatus(folderId: String, status: String, timestamp: Long)

    @Query("DELETE FROM folder")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(folders: List<Folder>)



    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM folder ORDER BY position ASC")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folder WHERE id = :folderId")
    suspend fun getFolderById(folderId: String): Folder?

    @Query("UPDATE folder SET name = :newName, updatedAt = :updatedAt WHERE id = :folderId")
    suspend fun renameFolder(folderId: String, newName: String, updatedAt: Long)

    @Query("UPDATE folder SET color = :newColor, updatedAt = :updatedAt WHERE id = :folderId")
    suspend fun updateFolderColor(folderId: String, newColor: String, updatedAt: Long)

    @Query("UPDATE folder SET count = :newCount WHERE id = :folderId")
    suspend fun updateNoteCount(folderId: String, newCount: Int)

    @Update
    suspend fun updateFolders(folders: List<Folder>)

    @Query("SELECT * FROM folder WHERE syncStatus = :syncStatus")
    suspend fun getFoldersBySyncStatus(syncStatus: String): List<Folder>

    @Query("UPDATE folder SET syncStatus = 'SYNCED', updatedAt = :timestamp")
    suspend fun markAllFoldersAsSynced(timestamp: Long)

    @Query("UPDATE folder SET syncStatus = 'SYNCED', updatedAt = :timestamp WHERE id = :folderId")
    suspend fun updateFolderSyncTimestamp(folderId: String, timestamp: Long)
}