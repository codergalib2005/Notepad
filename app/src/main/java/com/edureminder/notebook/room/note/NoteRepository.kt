package com.edureminder.notebook.room.note


import com.edureminder.notebook.preferences.notes.SortOrder
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val currentTime = System.currentTimeMillis()

    // Fetch a single note by its ID
    fun getOneNote(id: String): Flow<Note> {
        return noteDao.getOneNote(id)
    }

    // Get notes by status and folderId with sorting
    fun getNotesByStatusAndSort(
        status: Status,
        sortOrder: SortOrder,
        selectedFolderId: String,
        filterByLock: Boolean,
        locked: Boolean,
        selectedType: Type?
    ): Flow<List<Note>> {
        val lockedParam: Boolean? = if (filterByLock) locked else null
        return noteDao.getNotesByStatusAndSort(
            status = status,
            sortOrder = sortOrder.name, // Convert enum to String
            selectedFolderId = selectedFolderId,
            lockedParam = lockedParam,
            selectedType = selectedType
        )
    }

    // Upsert (Insert or Update) a note
    suspend fun upsertNote(note: Note) {
        noteDao.upsertNote(note)
    }


    // Delete a single note
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    // Update folderId for a batch of notes
    suspend fun updateFolderIdForNotes(noteIds: List<String>, newFolderId: String) {
        noteDao.updateFolderIdForNotes(noteIds, newFolderId, timestamp = currentTime)
    }

    // Update the status of multiple notes at once
    suspend fun updateStatusesForNotes(noteIds: List<String>, newStatus: Status) {
        noteDao.updateStatusesForNotes(noteIds, newStatus.name, System.currentTimeMillis())
    }

    // Delete a batch of notes
    suspend fun deleteNotes(noteIds: List<String>) {
        noteDao.deleteNotes(noteIds)
    }

    // Pin a note
    suspend fun pinNote(noteId: String) {
        noteDao.pinNote(noteId, timestamp = currentTime)  // Pin the selected note
    }

    // Unpin a note
    suspend fun unpinNote(noteId: String) {
        noteDao.unpinNote(noteId, timestamp = currentTime)  // Unpin other notes first
    }

    // Lock a note
    suspend fun lockNote(noteId: String) {
        noteDao.lockNote(noteId, timestamp = currentTime)  // Lock the selected note
    }

    // UnLock a note
    suspend fun unlockNote(noteId: String) {
        noteDao.unlockNote(noteId, timestamp = currentTime)  // UnLock other notes first
    }

    // Lock multiple notes
    suspend fun lockManyNotes(noteIds: List<String>) {
        noteDao.lockManyNotes(noteIds, timestamp = System.currentTimeMillis())
    }

    // Unlock multiple notes
    suspend fun unlockManyNotes(noteIds: List<String>) {
        noteDao.unlockManyNotes(noteIds, timestamp = System.currentTimeMillis())
    }

    fun getPendingSyncNoteCount(): Flow<Int> {
        return noteDao.getNoteCountBySyncStatus(SyncStatus.PENDING.name)
    }

    suspend fun getPendingSyncNotes(): List<Note> {
        return noteDao.getNotesBySyncStatus(SyncStatus.PENDING.name)
    }

    suspend fun markAllAsSynced(timestamp: Long) {
        noteDao.markAllAsSynced(timestamp)
    }

    suspend fun markFolderNotesAsSynced(folderId: String, timestamp: Long) {
        noteDao.markNotesByFolderAsSynced(folderId, timestamp)
    }

    suspend fun getNotesByFolderId(folderId: String): List<Note> {
        return noteDao.getNotesByFolderId(folderId)
    }

    suspend fun markNoteAsSynced(noteId: String, timestamp: Long) {
        noteDao.markNoteAsSynced(noteId, timestamp)
    }

    suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun restoreNotesSmartly(notes: List<Note>) {
        for (note in notes) {
            val existing = noteDao.getNoteById(note.id)
            if (existing != null) {
                // Drive version should fully replace local version
                noteDao.upsertNote(note)
            } else {
                // Insert new note as-is
                noteDao.upsertNote(note)
            }
        }
    }

    suspend fun getLatestNote(): Note? {
        return noteDao.getLatestNote()
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