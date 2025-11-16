package com.edureminder.easynotes.drive


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.edureminder.easynotes.R
import com.edureminder.easynotes.room.folder.FolderRepository
import com.edureminder.easynotes.room.note.NoteRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo("Backing up notes..."))

        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
            ?: return Result.failure()

        return try {
            val syncManager = DriveSyncManager(applicationContext, noteRepository, folderRepository)
            syncManager.uploadNotesBackup(account)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun createForegroundInfo(message: String): ForegroundInfo {
        val channelId = "backup_channel"
        val notificationId = 1001

        val channel = NotificationChannel(
            channelId,
            "Drive Backup",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Edureminder Backup")
            .setContentText(message)
            .setSmallIcon(R.drawable.diary)
            .setOngoing(true)
            .build()

        return ForegroundInfo(notificationId, notification)
    }
}