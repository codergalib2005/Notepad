package com.edureminder.easynotes.drive.pref

import com.edureminder.easynotes.room.note.Status
import com.edureminder.easynotes.room.note.SyncStatus


interface Syncable {
    val id: String
    var updatedAt: Long
    var syncStatus: SyncStatus
    var status: Status
}