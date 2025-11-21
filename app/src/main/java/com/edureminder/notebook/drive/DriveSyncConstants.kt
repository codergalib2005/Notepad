package com.edureminder.notebook.drive

import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.room.note.Note


object DriveSyncConstants {
    const val DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file"
    const val FOLDER_NAME = "EdureminderBackup"
    const val NOTES_JSON_NAME = "notes.json"
    const val FOLDERS_JSON_NAME = "folders.json"
    const val MIME_TYPE_JSON = "application/json"
}
data class BackupData(
    val notes: List<Note>,
    val folders: List<Folder>
)