package com.edureminder.notebook.room.folder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.edureminder.notebook.drive.pref.Syncable
import com.edureminder.notebook.room.note.Status
import com.edureminder.notebook.room.note.SyncStatus
import com.edureminder.notebook.utils.generateUniqueId

@Entity(
    tableName = "folder",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["status"]),
        Index(value = ["syncStatus"])
    ]
)
data class Folder(
    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String = generateUniqueId(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val color: String,

    @ColumnInfo(name = "count")
    val count: Int = 0,

    @ColumnInfo(name = "position")
    var position: Int = 0,

    @ColumnInfo(name = "status")
    override var status: Status = Status.ACTIVE,

    @ColumnInfo(name = "syncStatus")
    override var syncStatus: SyncStatus = SyncStatus.PENDING,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updatedAt")
    override var updatedAt: Long = System.currentTimeMillis()
) : Syncable