package com.edureminder.notebook.drive.pref

import com.edureminder.notebook.room.note.Status
import com.edureminder.notebook.room.note.SyncStatus


interface Syncable {
    val id: String
    var updatedAt: Long
    var syncStatus: SyncStatus
    var status: Status
}