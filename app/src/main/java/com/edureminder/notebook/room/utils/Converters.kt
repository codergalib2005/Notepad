package com.edureminder.notebook.room.utils

import androidx.room.TypeConverter
import com.edureminder.notebook.room.note.Status
import com.edureminder.notebook.room.note.SyncStatus

class Converters {
    @TypeConverter
    fun fromStatus(value: Status): String = value.name

    @TypeConverter
    fun toStatus(value: String): Status = Status.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}