package com.edureminder.easynotes.room.diary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.edureminder.easynotes.room.note.Status
import com.edureminder.easynotes.room.note.SyncStatus
import kotlinx.coroutines.flow.Flow

data class DiaryPreview(
    val id: String,
    val title: String,
    val preview: String,
    val createdAtDay: Int,
    val createdAtMonth: String,
    val createdAtYear: Int,
    val createdAtTime: String,
    val mood: Int,
    val images: String
)

data class DiaryRaw(
    val id: String,
    val title: String,
    val preview: String,
    val createdAt: Long,
    val mood: Int,
    val images: String
)


@Dao
interface DiaryDao {
    @Query("SELECT COUNT(*) FROM diary_table WHERE syncStatus = :syncStatus")
    fun getDiaryCountBySyncStatus(syncStatus: String): Flow<Int>


    // Upsert operation for inserting or updating a Diary
    @Upsert
    suspend fun upsertDiary(diary: Diary)

    /**
     * New
     */
    @Upsert
    suspend fun upsertDiaries(diaries: List<Diary>)

    @Query("""
        SELECT 
            *,
            SUBSTR(body, 1, 300) AS preview
        FROM diary_table
    """)
    suspend fun fetchAllDiaries(): List<DiaryRaw>

    @Query("SELECT * FROM diary_table WHERE status = 'ACTIVE' ORDER BY updatedAt DESC")
    fun getAllActiveDiaries(): Flow<List<Diary>>

    @Query("DELETE FROM diary_table")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diaries: List<Diary>)


    // Delete operation for removing a Diary
    @Delete
    suspend fun deleteDiary(diary: Diary)

    // Dynamic query to fetch notes based on folderId and sort order
//    @Query("""
//        SELECT * FROM diary_table
//        WHERE status = :status
//          AND (:lockedParam IS NULL OR locked = :lockedParam)
//          AND (:selectedFolderId = '0' OR folderId = :selectedFolderId)
//          AND (:selectedType IS NULL OR type = :selectedType)
//        ORDER BY 
//            CASE WHEN :sortOrder = 'TITLE_ASCENDING' THEN title END ASC,
//            CASE WHEN :sortOrder = 'TITLE_DESCENDING' THEN title END DESC,
//            CASE WHEN :sortOrder = 'DATE_OLDEST_FIRST' THEN createdAt END ASC,
//            CASE WHEN :sortOrder = 'DATE_NEWEST_FIRST' THEN createdAt END DESC,
//            CASE WHEN :sortOrder = 'UPDATED_AT_OLDEST_FIRST' THEN updatedAt END ASC,
//            CASE WHEN :sortOrder = 'UPDATED_AT_NEWEST_FIRST' THEN updatedAt END DESC,
//            id ASC
//    """)
//    fun getNotesByStatusAndSort(
//        status: Status,
//        sortOrder: String,
//        selectedFolderId: String,
//        lockedParam: Boolean?,
//        selectedType: Type?
//    ): Flow<List<Diary>>


    // Fetch a single Diary by its ID as Flow for reactive updates
    @Query("SELECT * FROM diary_table WHERE ID = :id")
    fun getOneDiary(id: String): Flow<Diary>

    // Update folderId for a batch of Diaries
    @Query("""
        UPDATE diary_table 
        SET folderId = :newFolderId, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:diaryIds)
    """)
    suspend fun updateFolderIdForDiaries(diaryIds: List<String>, newFolderId: String, timestamp: Long)

    // Update status for a batch of Diary, with timestamp
    @Query("""
        UPDATE diary_table 
        SET status = :newStatus, 
            updatedAt = :newTimestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:diaryIds)
    """)
    suspend fun updateStatusesForDiaries(diaryIds: List<String>, newStatus: String, newTimestamp: Long)

    // Bulk delete Diaries by IDs
    @Query("DELETE FROM diary_table WHERE ID IN (:diaryIds)")
    suspend fun deleteDiaries(diaryIds: List<String>)

    // Pin a Diary
    @Query("""
        UPDATE diary_table 
        SET favourite = 1, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :diaryId
    """)
    suspend fun pinDiary(diaryId: String, timestamp: Long)

    // Unpin a Diary
    @Query("""
        UPDATE diary_table 
        SET favourite = 0, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :diaryId
    """)
    suspend fun unpinDiary(diaryId: String, timestamp: Long)

    // Lock a Diary
    @Query("""
        UPDATE diary_table 
        SET locked = 1, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :diaryId
    """)
    suspend fun lockDiary(diaryId: String, timestamp: Long)

    // Unlock a Diary
    @Query("""
        UPDATE diary_table 
        SET locked = 0, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :diaryId
    """)
    suspend fun unlockDiary(diaryId: String, timestamp: Long)

    // Lock multiple Diary
    @Query("""
        UPDATE diary_table 
        SET locked = 1, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:diaryIds)
    """)
    suspend fun lockManyDiaries(diaryIds: List<String>, timestamp: Long)

    // Unlock multiple Diary
    @Query("""
        UPDATE diary_table 
        SET locked = 0, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:diaryIds)
    """)
    suspend fun unlockManyDiaries(diaryIds: List<String>, timestamp: Long)

    @Query("SELECT * FROM diary_table WHERE syncStatus = :syncStatus")
    suspend fun getDiariesBySyncStatus(syncStatus: String): List<Diary>

    @Query("UPDATE diary_table SET syncStatus = 'SYNCED', updatedAt = :timestamp")
    suspend fun markAllAsSynced(timestamp: Long)

    @Query("UPDATE diary_table SET syncStatus = 'SYNCED', updatedAt = :timestamp WHERE folderId = :folderId")
    suspend fun markDiariesByFolderAsSynced(folderId: String, timestamp: Long)

    @Query("SELECT * FROM diary_table WHERE folderId = :folderId AND status = 'ACTIVE'")
    suspend fun getDiariesByFolderId(folderId: String): List<Diary>

    @Query("UPDATE diary_table SET syncStatus = 'SYNCED', updatedAt = :timestamp WHERE id = :diaryId")
    suspend fun markDiaryAsSynced(diaryId: String, timestamp: Long)

    @Query("SELECT * FROM diary_table WHERE id = :diaryId")
    suspend fun getDiaryById(diaryId: String): Diary?

    @Query("SELECT * FROM diary_table WHERE id = :id LIMIT 1")
    fun getADiaryById(id: String): Diary?
    

    @Query("SELECT * FROM diary_table WHERE status = :status ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestDiary(status: String = Status.ACTIVE.name): Diary?

}