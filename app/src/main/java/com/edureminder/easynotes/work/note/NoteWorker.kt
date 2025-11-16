package com.edureminder.easynotes.work.note

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edureminder.easynotes.R
import com.edureminder.easynotes.room.AppDatabase
import com.edureminder.easynotes.room.note.Type
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class NoteWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val noteIdStr = inputData.getString("NOTE_ID") ?: ""
        if (noteIdStr.isEmpty()) return Result.failure()

        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val noteDao = db.noteDao()
            val note = noteDao.getANoteById(noteIdStr)

            if (note == null) {
                Log.e("NoteWorker", "Note not found")
                return Result.failure()
            }

            showNotification(noteIdStr, note.title ?: "Untitled", note.type.name)
            wakeUpScreen()

            // ✅ Calculate next reminder date
            val repeatDays = note.repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
            if (repeatDays.isNotEmpty()) {
                val nextDate = findNextDate(repeatDays)

                // ✅ Update note
                val updatedNote = note.copy(
                    reminderDate = nextDate,
                )
                noteDao.upsertNote(updatedNote)

                // ✅ Schedule new worker for next reminder
                scheduleExactNoteWorker(
                    context = applicationContext,
                    uniqueWorkId = noteIdStr,
                    timeString = note.reminderTime,
                    selectedDaysString = note.repeatDays,
                    date = nextDate
                )
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("NoteWorker", "Error in doWork: ${e.localizedMessage}", e)
            Result.failure()
        }
    }


    private fun showNotification(noteId: String, todoTitle: String, noteType: String) {
        val channelId = "note_reminder_channel"
        val channelName = "Note Reminders"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
        }
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        // val deepLinkUri = "notepad://home/"
        /**
         * uriPattern = "notepad://note/{noteID}"
         */
        val deepLinkUri = when (noteType) {
            Type.CHECKLIST.name -> "checklists://checklist/$noteId".toUri()
            else -> "notepad://note/$noteId".toUri()
        }
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.diary)
            .setContentTitle(todoTitle)
            .setContentText("Tab to open")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(todoTitle.hashCode(), notification)
            }
        } else {
            Log.w("NoteWorker", "Notification permission not granted")
        }
    }

    private fun wakeUpScreen() {
        val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "MyApp::NoteWakeLock"
        )
        wakeLock.acquire(3000)
    }
}

fun findNextDate(repeatDays: List<Int>): String {
    val today = LocalDate.now()
    var nextDate = today.plusDays(1)

    while (true) {
        val dayOfWeek = nextDate.dayOfWeek.toCalendarDayOfWeek()
        if (repeatDays.contains(dayOfWeek)) {
            break
        }
        nextDate = nextDate.plusDays(1)
    }

    return nextDate.toString() // Format: "yyyy-MM-dd"
}