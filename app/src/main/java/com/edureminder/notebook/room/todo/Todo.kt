package com.edureminder.notebook.room.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edureminder.notebook.room.note.SyncStatus
import com.edureminder.notebook.utils.generateUniqueId

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey
    @ColumnInfo(name = "id") override var id: String = generateUniqueId(),

    @ColumnInfo(name = "title", defaultValue = "") var title: String = "",

    @ColumnInfo(name = "status", defaultValue = "PENDING")
    override var status: TodoStatus = TodoStatus.PENDING,

    @ColumnInfo(name = "date", defaultValue = "") var date: String = "",
    @ColumnInfo(name = "time", defaultValue = "") var time: String = "",
    @ColumnInfo(name = "description", defaultValue = "") var description: String = "",
    @ColumnInfo(name = "subtask", defaultValue = "") var subtask: String = "",
    @ColumnInfo(name = "flag_id", defaultValue = "0") var flagId: Int = 0,
    @ColumnInfo(name = "folderId") val folderId: String = "0",
    @ColumnInfo(name = "repeat_days", defaultValue = "") var repeatDays: String = "",
    @ColumnInfo(name = "prev_todo_id", defaultValue = "0") var prevTodoId: String = "",
    @ColumnInfo(name = "reminder_type", defaultValue = "1") var reminderType: Int = 2,
    @ColumnInfo(name = "notificationID", defaultValue = "0") var notificationID: Int = 0,

    @ColumnInfo(name = "syncStatus") override var syncStatus: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo(name = "lastSyncedAt") var lastSyncedAt: Long? = null,
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updatedAt") override var updatedAt: Long = System.currentTimeMillis(),
) : Syncable

interface Syncable {
    val id: String
    var status: TodoStatus
    var updatedAt: Long
    var syncStatus: SyncStatus
}
enum class TodoStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}

enum class NotificationStatus {
    DONT,
    NOTIFICATION,
    ALARM,
}

fun getNextStatus(current: TodoStatus): TodoStatus {
    return when (current) {
        TodoStatus.PENDING -> TodoStatus.COMPLETED
        TodoStatus.COMPLETED -> TodoStatus.CANCELLED
        TodoStatus.CANCELLED -> TodoStatus.PENDING
    }
}