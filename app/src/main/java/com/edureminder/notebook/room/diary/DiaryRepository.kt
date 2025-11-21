package com.edureminder.notebook.room.diary


import android.annotation.SuppressLint
import com.edureminder.notebook.presentation.screen.main_screen.diary_views.SortOrder
import com.edureminder.notebook.room.note.Status
import com.edureminder.notebook.room.note.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class DiaryRepository(private val diaryDao: DiaryDao) {
    val currentTime = System.currentTimeMillis()

    // Fetch a single note by its ID
    fun getOneDiary(id: String): Flow<Diary> {
        return diaryDao.getOneDiary(id)
    }
    @SuppressLint("DefaultLocale")
    fun mapDiaryRawToPreview(raw: DiaryRaw, use24HourFormat: Boolean): DiaryPreview {
        val calendar = Calendar.getInstance().apply { timeInMillis = raw.createdAt }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timeString = if (use24HourFormat) {
            String.format("%02d:%02d", hour, minute)
        } else {
            val amPm = if (hour >= 12) "PM" else "AM"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12
            String.format("%d:%02d %s", hour12, minute, amPm)
        }

        // Get short month name
        val monthShort = calendar.getDisplayName(
            Calendar.MONTH,
            Calendar.SHORT,
            java.util.Locale.getDefault()
        ) ?: "Jan"

        return DiaryPreview(
            id = raw.id,
            title = raw.title,
            preview = raw.preview,
            createdAtDay = calendar.get(Calendar.DAY_OF_MONTH),
            createdAtMonth = monthShort, // <-- use short name
            createdAtYear = calendar.get(Calendar.YEAR),
            createdAtTime = timeString,
            images = raw.images,
            mood = raw.mood
        )
    }
    suspend fun getAllDiariesFlow(
        search: String? = null,
        folderId: String? = null,
        createdAfter: Long? = null,
        createdBefore: Long? = null,
        updatedAfter: Long? = null,
        updatedBefore: Long? = null,
        sortBy: String? = SortOrder.DATE_NEWEST_FIRST.value,
        use24HourFormat: Boolean = false
    ): Flow<List<DiaryPreview>> {
        return diaryDao.fetchFilteredDiaries(
            search,
            folderId,
            createdAfter,
            createdBefore,
            updatedAfter,
            updatedBefore,
            sortBy
        ).map { rawList ->
            rawList.map { raw -> mapDiaryRawToPreview(raw, use24HourFormat) }
        }
    }





    // Upsert (Insert or Update) a note
    suspend fun upsertDiary(diary: Diary) {
        diaryDao.upsertDiary(diary)
    }


    // Delete a single note
    suspend fun deleteDiary(diary: Diary) {
        diaryDao.deleteDiary(diary)
    }

    // Update folderId for a batch of notes
    suspend fun updateFolderIdForDiaries(diaryIds: List<String>, newFolderId: String) {
        diaryDao.updateFolderIdForDiaries(diaryIds, newFolderId, timestamp = currentTime)
    }

    // Update the status of multiple notes at once
    suspend fun updateStatusesForDiaries(diaryIds: List<String>, newStatus: Status) {
        diaryDao.updateStatusesForDiaries(diaryIds, newStatus.name, System.currentTimeMillis())
    }

    // Delete a batch of notes
    suspend fun deleteDiaries(diaryIds: List<String>) {
        diaryDao.deleteDiaries(diaryIds)
    }

    // Pin a note
    suspend fun pinDiary(diaryId: String) {
        diaryDao.pinDiary(diaryId, timestamp = currentTime)  // Pin the selected note
    }

    // Unpin a note
    suspend fun unpinDiary(diaryId: String) {
        diaryDao.unpinDiary(diaryId, timestamp = currentTime)  // Unpin other notes first
    }

    // Lock a note
    suspend fun lockDiary(diaryId: String) {
        diaryDao.lockDiary(diaryId, timestamp = currentTime)  // Lock the selected note
    }

    // UnLock a note
    suspend fun unlockDiary(diaryId: String) {
        diaryDao.unlockDiary(diaryId, timestamp = currentTime)  // UnLock other notes first
    }

    // Lock multiple notes
    suspend fun lockManyDiaries(diaryIds: List<String>) {
        diaryDao.lockManyDiaries(diaryIds, timestamp = System.currentTimeMillis())
    }

    // Unlock multiple notes
    suspend fun unlockManyDiaries(diaryIds: List<String>) {
        diaryDao.unlockManyDiaries(diaryIds, timestamp = System.currentTimeMillis())
    }

    fun getPendingSyncDiaryCount(): Flow<Int> {
        return diaryDao.getDiaryCountBySyncStatus(SyncStatus.PENDING.name)
    }

    suspend fun getPendingSyncDiaries(): List<Diary> {
        return diaryDao.getDiariesBySyncStatus(SyncStatus.PENDING.name)
    }

    suspend fun markAllAsSynced(timestamp: Long) {
        diaryDao.markAllAsSynced(timestamp)
    }

    suspend fun markFolderDiariesAsSynced(folderId: String, timestamp: Long) {
        diaryDao.markDiariesByFolderAsSynced(folderId, timestamp)
    }

    suspend fun getDiariesByFolderId(folderId: String): List<Diary> {
        return diaryDao.getDiariesByFolderId(folderId)
    }

    suspend fun markNoteAsSynced(diaryId: String, timestamp: Long) {
        diaryDao.markDiaryAsSynced(diaryId, timestamp)
    }

    suspend fun getDiaryById(id: String): Diary? {
        return diaryDao.getDiaryById(id)
    }

    suspend fun restoreDiariesSmartly(diaries: List<Diary>) {
        for (diary in diaries) {
            val existing = diaryDao.getDiaryById(diary.id)
            if (existing != null) {
                // Drive version should fully replace local version
                diaryDao.upsertDiary(diary)
            } else {
                // Insert new note as-is
                diaryDao.upsertDiary(diary)
            }
        }
    }

    suspend fun getLatestDiary(): Diary? {
        return diaryDao.getLatestDiary()
    }




    /**
     * Notes
     */
//    suspend fun upsertNotes(notes: List<Note>) {
//        noteDao.upsertNotes(notes)
//    }
//
//    suspend fun getPendingSyncNotes(): List<Note> {
//        return noteDao.getPendingSyncNotes()
//    }
//
//    suspend fun fetchAllActiveNotes(): List<Note> {
//        return noteDao.fetchAllActiveNotes()
//    }
//
//    suspend fun markAllAsSynced(timestamp: Long) {
//        noteDao.markAllAsSynced(timestamp)
//    }

}