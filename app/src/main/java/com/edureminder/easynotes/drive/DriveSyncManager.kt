package com.edureminder.easynotes.drive

import android.content.Context
import android.util.Log
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.folder.FolderRepository
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteRepository
import com.edureminder.easynotes.room.note.SyncStatus
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream
import java.io.File

class DriveSyncManager(
    private val context: Context,
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository
) {

    // Initialize Google Drive service
    private fun getDriveService(account: GoogleSignInAccount?): Drive? {
        return account?.let {
            val credential = GoogleAccountCredential.usingOAuth2(
                context, listOf(DriveSyncConstants.DRIVE_SCOPE)
            ).apply { selectedAccount = it.account }

            Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("Edureminder")
                .build()
        }
    }

    // Upload each note as {noteId}.json inside /backup/notes/ and save folders.json in root
    suspend fun uploadNotesBackup(account: GoogleSignInAccount) {
        val drive = getDriveService(account) ?: return

        // Step 1: Get or create main backup folder
        val rootFolderId = getOrCreateBackupFolder(drive)

        // Step 2: Upload folders list as folders.json
        val folders = folderRepository.getAllFolders().first()
        val foldersJson = Gson().toJson(folders)
        val foldersFile = File(context.cacheDir, DriveSyncConstants.FOLDERS_JSON_NAME).apply {
            writeText(foldersJson)
        }

        // Step 3: Delete existing folders.json if exists
        searchFile(drive, DriveSyncConstants.FOLDERS_JSON_NAME, parentFolderId = rootFolderId)?.forEach {
            drive.files().delete(it.id).execute()
        }

        // Step 4: Upload new folders.json
        val foldersMetadata = com.google.api.services.drive.model.File().apply {
            name = DriveSyncConstants.FOLDERS_JSON_NAME
            parents = listOf(rootFolderId)
            mimeType = DriveSyncConstants.MIME_TYPE_JSON
        }
        val foldersFileContent = FileContent(DriveSyncConstants.MIME_TYPE_JSON, foldersFile)
        drive.files().create(foldersMetadata, foldersFileContent).execute()

        // Step 5: Create /backup/notes subfolder
        val notesFolderId = getOrCreateSubFolder(drive, rootFolderId, "notes")

        // Step 6: Upload each note as individual JSON file
        val notes = noteRepository.getPendingSyncNotes()
        for (note in notes) {
            val json = Gson().toJson(note.copy(
                syncStatus = SyncStatus.SYNCED
            ))
            val fileName = "${note.id}.json"
            val file = File(context.cacheDir, fileName).apply { writeText(json) }

            // Step 7: Delete existing note file if exists
            searchFile(drive, fileName, notesFolderId)?.forEach {
                drive.files().delete(it.id).execute()
            }

            // Step 8: Upload note file
            // Step 8: Upload note file
            val noteMetadata = com.google.api.services.drive.model.File().apply {
                name = fileName
                parents = listOf(notesFolderId)
                mimeType = DriveSyncConstants.MIME_TYPE_JSON
            }
            val noteFileContent = FileContent(DriveSyncConstants.MIME_TYPE_JSON, file)
            drive.files().create(noteMetadata, noteFileContent).execute()

            // Step 9: Mark note as synced
            noteRepository.markNoteAsSynced(note.id, System.currentTimeMillis())
        }
    }


    // Get or create main backup folder
    private fun getOrCreateBackupFolder(drive: Drive): String {
        val query = "mimeType = 'application/vnd.google-apps.folder' and name = '${DriveSyncConstants.FOLDER_NAME}'"
        val result = drive.files().list().setQ(query).execute().files
        return result?.firstOrNull()?.id ?: run {
            val metadata = com.google.api.services.drive.model.File().apply {
                name = DriveSyncConstants.FOLDER_NAME
                mimeType = "application/vnd.google-apps.folder"
            }
            drive.files().create(metadata).execute().id
        }
    }

    // Get or create a subfolder inside a parent folder
    suspend fun getOrCreateSubFolder(
        drive: Drive,
        parentFolderId: String,
        subFolderName: String
    ): String {
        val query = "mimeType='application/vnd.google-apps.folder' and trashed=false and name='$subFolderName' and '$parentFolderId' in parents"
        val result = drive.files().list().setQ(query).setSpaces("drive").execute()
        return result.files.firstOrNull()?.id ?: run {
            val metadata = com.google.api.services.drive.model.File().apply {
                name = subFolderName
                mimeType = "application/vnd.google-apps.folder"
                parents = listOf(parentFolderId)
            }
            drive.files().create(metadata).execute().id
        }
    }

    // Search for a file by name inside a parent folder
    private fun searchFile(
        drive: Drive,
        name: String,
        parentFolderId: String? = null
    ): List<com.google.api.services.drive.model.File>? {
        val query = buildString {
            append("name = '$name' and trashed = false")
            if (parentFolderId != null) append(" and '$parentFolderId' in parents")
        }
        return drive.files().list().setQ(query).execute().files
    }








    // Restore folders and notes from one .json backup (if exists)
    suspend fun restoreBackup(account: GoogleSignInAccount) {
        val drive = getDriveService(account) ?: return

        // Step 1: Restore Folders
        val folderFiles = searchFile(drive, DriveSyncConstants.FOLDERS_JSON_NAME)
        val folderBackupFile = folderFiles?.firstOrNull() ?: return

        val folderOutputStream = ByteArrayOutputStream()
        drive.files().get(folderBackupFile.id).executeMediaAndDownloadTo(folderOutputStream)

        val folderJson = folderOutputStream.toString()
        val folderListType = object : TypeToken<List<Folder>>() {}.type
        val folders: List<Folder> = Gson().fromJson(folderJson, folderListType)

        Log.d("Tag1", "Restoring folders -> $folders")
        folderRepository.restoreFoldersSmartly(folders)

        // Step 2: Look for "notes" subfolder
        val backupRootFolder = getOrCreateBackupFolder(drive)
        val notesFolder = getOrCreateSubFolder(drive, backupRootFolder, "notes")

        // Step 3: List all files in the "notes" folder
        val noteFiles = listFilesInFolder(drive, notesFolder)
        val restoredNotes = mutableListOf<Note>()


        for (noteFile in noteFiles) {
            val outputStream = ByteArrayOutputStream()
            drive.files().get(noteFile.id).executeMediaAndDownloadTo(outputStream)

            val noteJson = outputStream.toString()
            val note: Note = Gson().fromJson(noteJson, Note::class.java)
            Log.d("Tag2", "Parsed note: $note")
            restoredNotes.add(note)
            // 👉 Here you can deserialize and restore notes to DB
            // Example:
            // val noteListType = object : TypeToken<List<Note>>() {}.type
            // val notes: List<Note> = Gson().fromJson(noteJson, noteListType)
            // notesRepository.restoreNotesSmartly(notes)
        }
        noteRepository.restoreNotesSmartly(restoredNotes)

    }

    suspend fun listFilesInFolder(drive: Drive, folderId: String): List<com.google.api.services.drive.model.File> {
        val result = mutableListOf<com.google.api.services.drive.model.File>()
        var pageToken: String? = null

        do {
            val request = drive.files().list().apply {
                q = "'$folderId' in parents and trashed = false"
                spaces = "drive"
                fields = "nextPageToken, files(id, name, mimeType)"
                this.pageToken = pageToken
            }

            val fileList = request.execute()
            result.addAll(fileList.files)
            pageToken = fileList.nextPageToken
        } while (pageToken != null)

        return result
    }

    suspend fun deleteUserDataFromDrive(account: GoogleSignInAccount) {
        val drive = getDriveService(account) ?: return

        val query = "mimeType = 'application/vnd.google-apps.folder' and name = '${DriveSyncConstants.FOLDER_NAME}' and trashed = false"
        val result = drive.files().list().setQ(query).execute().files

        result?.firstOrNull()?.let { backupFolder ->
            drive.files().delete(backupFolder.id).execute()
            Log.d("Log1", "Deleted backup folder: ${backupFolder.name}")
        } ?: Log.d("Log1", "No backup folder found to delete.")
    }

}