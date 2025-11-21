package com.edureminder.notebook.room.note

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edureminder.notebook.drive.pref.Syncable
import com.edureminder.notebook.utils.generateUniqueId

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey
    @ColumnInfo(name = "id") override val id: String = generateUniqueId(),

    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "body") val body: String = "",
    @ColumnInfo(name = "favourite") val isFavourite: Boolean = false,
    @ColumnInfo(name = "locked") val isLocked: Boolean = false,
    @ColumnInfo(name = "status") override var status: Status = Status.ACTIVE,
    @ColumnInfo(name = "type") val type: Type = Type.NOTE,
    @ColumnInfo(name = "folderId") val folderId: String = "0",
    @ColumnInfo(name = "themeId") val themeId: Int = 1,
    @ColumnInfo(name = "syncStatus") override var syncStatus: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo(name = "lastSyncedAt") var lastSyncedAt: Long? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updatedAt") override var updatedAt: Long = System.currentTimeMillis(),

    /**
     * Reminder
     */
    @ColumnInfo(name = "reminder_time") val reminderTime: String = "",
    @ColumnInfo(name = "reminder_date") val reminderDate: String = "",
    @ColumnInfo(name = "reminder_type") val reminderType: Int = 1,
    @ColumnInfo(name = "repeat_days", defaultValue = "") var repeatDays: String = ""
) : Syncable


enum class SyncStatus {
    PENDING,
    SYNCED,
    FAILED
}

enum class Status {
    ACTIVE,
    ARCHIVED,
    DELETED,
}

enum class Type {
    NOTE,
    CHECKLIST;

    companion object {
        fun fromString(value: String): Type? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}