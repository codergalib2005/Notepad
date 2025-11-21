package com.edureminder.notebook.room.diary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edureminder.notebook.drive.pref.Syncable
import com.edureminder.notebook.room.note.Status
import com.edureminder.notebook.room.note.SyncStatus
import com.edureminder.notebook.utils.generateUniqueId

@Entity(tableName = "diary_table")
data class Diary(
    @PrimaryKey
    @ColumnInfo(name = "id") override val id: String = generateUniqueId(),

    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "body") val body: String = "",
    @ColumnInfo(name = "favourite") val isFavourite: Boolean = false,
    @ColumnInfo(name = "status") override var status: Status = Status.ACTIVE,
    @ColumnInfo(name = "folderId") val folderId: String = "0",
    @ColumnInfo(name = "locked") val isLocked: Boolean = false,
    @ColumnInfo(name = "backgroundId") val backgroundId: Int = 1,
    @ColumnInfo(name = "stickers") val stickers: String = "",
    @ColumnInfo(name = "images") val images: String = "",
    @ColumnInfo(name = "syncStatus") override var syncStatus: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo(name = "lastSyncedAt") var lastSyncedAt: Long? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updatedAt") override var updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "mood") val mood: Int = 1,

    /**
     * Reminder
     */
    @ColumnInfo(name = "reminder_time") val reminderTime: String = "",
    @ColumnInfo(name = "reminder_date") val reminderDate: String = "",
    @ColumnInfo(name = "reminder_type") val reminderType: Int = 1,
    @ColumnInfo(name = "repeat_days", defaultValue = "") var repeatDays: String = ""
) : Syncable
