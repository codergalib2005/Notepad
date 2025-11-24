package com.edureminder.notebook.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.room.Room
import com.edureminder.notebook.MainActivity
import com.edureminder.notebook.R
import com.edureminder.notebook.room.AppDatabase
import com.edureminder.notebook.room.todo.TodoStatus
import com.edureminder.notebook.utils.generateUniqueId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

const val notificationID = 1
const val channelID = "Task Reminder Channel v1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"
const val notificationIdExtra = "notificationIdExtra"
const val notificationTypeExtra = "notificationTypeExtra"


class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        try {
            if (intent == null) {
                Log.e("Notification", "Intent is null — cannot process")
                return
            }

            val db = try {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "draftly_database"
                ).build()
            } catch (e: Exception) {
                Log.e("Notification", "Database initialization failed: ${e.message}")
                return
            }

            val title = intent.getStringExtra(titleExtra) ?: "Reminder"
            val message = intent.getStringExtra(messageExtra) ?: "Tap to open"
            val notificationId = intent.getStringExtra(notificationIdExtra)
                ?: run {
                    Log.e("Notification", "notificationIdExtra is null")
                    return
                }

            val notificationType = intent.getStringExtra(notificationTypeExtra) ?: "UNKNOWN"

            // --- SAFE PENDING INTENT
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = try {
                PendingIntent.getActivity(
                    context.applicationContext,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            } catch (e: Exception) {
                Log.e("Notification", "PendingIntent failed: ${e.message}")
                null
            }

            // --- SHOW NOTIFICATION SAFELY
            try {
                val builder = NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.note)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)

                if (pendingIntent != null) {
                    builder.setContentIntent(pendingIntent)
                }

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val randomId = (Math.random() * 9999).toInt()
                manager.notify(randomId, builder.build())

                Log.d("Notification", "Notification fired for ID $notificationId")

            } catch (e: Exception) {
                Log.e("Notification", "Error showing notification: ${e.message}")
            }

            // --- DATABASE + REPEAT LOGIC SAFELY ---
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val todoDao = db.todoDao()
                    val getTodo = todoDao.getOneTodoSafe(notificationId)

                    if (getTodo == null) {
                        Log.e("Notification", "Todo not found, id=$notificationId")
                        return@launch
                    }

                    // Parse repeat days safely
                    val repeatDays = getTodo.repeatDays
                        .split(",")
                        .mapNotNull { it.toIntOrNull() }

                    if (repeatDays.isEmpty()) {
                        Log.d("Notification", "No repeat days. Finished.")
                        return@launch
                    }

                    // Ensure no duplicate future Todo exists
                    val existingNext = todoDao.findTodoByPrevTodoId(getTodo.id)
                    if (existingNext != null) return@launch

                    val currentDate = runCatching { LocalDate.parse(getTodo.date) }.getOrElse {
                        Log.e("Notification", "Invalid date: ${getTodo.date}")
                        return@launch
                    }

                    // Compute next repeat day
                    var nextDate = currentDate.plusDays(1)
                    var safetyCounter = 0
                    while (!repeatDays.contains(nextDate.dayOfWeek.value)) {
                        nextDate = nextDate.plusDays(1)
                        safetyCounter++
                        if (safetyCounter > 10) break
                    }

                    // Safe time parsing
                    val localTime = try {
                        LocalTime.parse(getTodo.time) // expected HH:mm
                    } catch (e: Exception) {
                        val parts = getTodo.time.split(":")
                        val hour = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
                        val minute = parts.getOrNull(1)?.padStart(2, '0') ?: "00"
                        LocalTime.parse("$hour:$minute")
                    }

                    val dateTime = LocalDateTime.of(nextDate, localTime)
                    val triggerMillis = dateTime.atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    val newTodo = getTodo.copy(
                        id = generateUniqueId(),
                        date = nextDate.toString(),
                        status = TodoStatus.PENDING,
                        prevTodoId = getTodo.id
                    )

                    val newId = todoDao.addTodo(newTodo)

                    scheduleNotification(
                        context,
                        newTodo.title,
                        "Tap to open",
                        triggerMillis,
                        newTodo
                    )

                    Log.d("Notification", "New repeated Todo created: $newId for $nextDate")

                } catch (e: Exception) {
                    Log.e("Notification", "Repeat scheduling error: ${e.message}")
                }
            }

        } catch (e: Exception) {
            Log.e("Notification", "Fatal error in BroadcastReceiver: ${e.message}")
        }
    }
}
