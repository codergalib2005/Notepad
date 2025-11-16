package com.edureminder.easynotes.room.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT COUNT(*) FROM notes_table WHERE syncStatus = :syncStatus")
    fun getNoteCountBySyncStatus(syncStatus: String): Flow<Int>


    // Upsert operation for inserting or updating a note
    @Upsert
    suspend fun upsertNote(note: Note)

    /**
     * New
     */
    @Upsert
    suspend fun upsertNotes(notes: List<Note>)

    @Query("SELECT * FROM notes_table")
    suspend fun fetchAllNotes(): List<Note>

    @Query("SELECT * FROM notes_table WHERE status = 'ACTIVE' ORDER BY updatedAt DESC")
    fun getAllActiveNotes(): Flow<List<Note>>

    @Query("DELETE FROM notes_table")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)


    // Delete operation for removing a note
    @Delete
    suspend fun deleteNote(note: Note)

    // Dynamic query to fetch notes based on folderId and sort order
    @Query("""
        SELECT * FROM notes_table
        WHERE status = :status
          AND (:lockedParam IS NULL OR locked = :lockedParam)
          AND (:selectedFolderId = '0' OR folderId = :selectedFolderId)
          AND (:selectedType IS NULL OR type = :selectedType)
        ORDER BY 
            CASE WHEN :sortOrder = 'TITLE_ASCENDING' THEN title END ASC,
            CASE WHEN :sortOrder = 'TITLE_DESCENDING' THEN title END DESC,
            CASE WHEN :sortOrder = 'DATE_OLDEST_FIRST' THEN createdAt END ASC,
            CASE WHEN :sortOrder = 'DATE_NEWEST_FIRST' THEN createdAt END DESC,
            CASE WHEN :sortOrder = 'UPDATED_AT_OLDEST_FIRST' THEN updatedAt END ASC,
            CASE WHEN :sortOrder = 'UPDATED_AT_NEWEST_FIRST' THEN updatedAt END DESC,
            id ASC
    """)
    fun getNotesByStatusAndSort(
        status: Status,
        sortOrder: String,
        selectedFolderId: String,
        lockedParam: Boolean?,
        selectedType: Type?
    ): Flow<List<Note>>


    // Fetch a single note by its ID as Flow for reactive updates
    @Query("SELECT * FROM notes_table WHERE ID = :id")
    fun getOneNote(id: String): Flow<Note>

    // Update folderId for a batch of notes
    @Query("""
        UPDATE notes_table 
        SET folderId = :newFolderId, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:noteIds)
    """)
    suspend fun updateFolderIdForNotes(noteIds: List<String>, newFolderId: String, timestamp: Long)

    // Update status for a batch of notes, with timestamp
    @Query("""
        UPDATE notes_table 
        SET status = :newStatus, 
            updatedAt = :newTimestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:noteIds)
    """)
    suspend fun updateStatusesForNotes(noteIds: List<String>, newStatus: String, newTimestamp: Long)

    // Bulk delete notes by IDs
    @Query("DELETE FROM notes_table WHERE ID IN (:noteIds)")
    suspend fun deleteNotes(noteIds: List<String>)

    // Pin a note
    @Query("""
        UPDATE notes_table 
        SET favourite = 1, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :noteId
    """)
    suspend fun pinNote(noteId: String, timestamp: Long)

    // Unpin a note
    @Query("""
        UPDATE notes_table 
        SET favourite = 0, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :noteId
    """)
    suspend fun unpinNote(noteId: String, timestamp: Long)

    // Lock a note
    @Query("""
        UPDATE notes_table 
        SET locked = 1, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :noteId
    """)
    suspend fun lockNote(noteId: String, timestamp: Long)

    // Unlock a note
    @Query("""
        UPDATE notes_table 
        SET locked = 0, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id = :noteId
    """)
    suspend fun unlockNote(noteId: String, timestamp: Long)

    // Lock multiple notes
    @Query("""
        UPDATE notes_table 
        SET locked = 1, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:noteIds)
    """)
    suspend fun lockManyNotes(noteIds: List<String>, timestamp: Long)

    // Unlock multiple notes
    @Query("""
        UPDATE notes_table 
        SET locked = 0, 
            updatedAt = :timestamp, 
            syncStatus = 'PENDING' 
        WHERE id IN (:noteIds)
    """)
    suspend fun unlockManyNotes(noteIds: List<String>, timestamp: Long)

    @Query("SELECT * FROM notes_table WHERE syncStatus = :syncStatus")
    suspend fun getNotesBySyncStatus(syncStatus: String): List<Note>

    @Query("UPDATE notes_table SET syncStatus = 'SYNCED', updatedAt = :timestamp")
    suspend fun markAllAsSynced(timestamp: Long)

    @Query("UPDATE notes_table SET syncStatus = 'SYNCED', updatedAt = :timestamp WHERE folderId = :folderId")
    suspend fun markNotesByFolderAsSynced(folderId: String, timestamp: Long)

    @Query("SELECT * FROM notes_table WHERE folderId = :folderId AND status = 'ACTIVE'")
    suspend fun getNotesByFolderId(folderId: String): List<Note>

    @Query("UPDATE notes_table SET syncStatus = 'SYNCED', updatedAt = :timestamp WHERE id = :noteId")
    suspend fun markNoteAsSynced(noteId: String, timestamp: Long)

    @Query("SELECT * FROM notes_table WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): Note?

    @Query("SELECT * FROM notes_table WHERE id = :id LIMIT 1")
    fun getANoteById(id: String): Note?



    @Query("SELECT * FROM notes_table WHERE status = :status ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestNote(status: String = Status.ACTIVE.name): Note?

}