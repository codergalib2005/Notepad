package com.edureminder.easynotes.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.room.Room
import com.edureminder.easynotes.MainActivity
import com.edureminder.easynotes.R
import com.edureminder.easynotes.room.AppDatabase
import com.edureminder.easynotes.room.todo.TodoStatus
import com.edureminder.easynotes.utils.generateUniqueId
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
        if (intent == null) return
        val todoDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "notepad_database"
        ).build()


        val title = intent.getStringExtra(titleExtra)
        val message = intent.getStringExtra(messageExtra)
        val notificationId = intent.getStringExtra(notificationIdExtra)!!
        val notificationType = intent.getStringExtra(notificationTypeExtra)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // 🔥 Build deep link URI
        val deepLinkUri = when(notificationType){
            "TODO" -> "notepad://todo/$notificationId".toUri()
            "NOTE" -> "notepad://note/$notificationId".toUri()
            else -> "notepad://folder/$notificationId".toUri()
        }

//        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }

        val pendingIntent = PendingIntent.getActivity(
            context.applicationContext,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.note)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((Math.random() * 2000).toInt(), notification) // use your Todo ID
        Log.d("Log1", "Notification triggered with ID -> $notificationId")

        // --- DATABASE WORK ---
        CoroutineScope(Dispatchers.IO).launch {
            // Example: Mark the todo as "notified"
            val todoDao = todoDatabase.todoDao()
            val getTodo = todoDao.getOneTodo(notificationId)

            val repeatDays = getTodo.repeatDays.split(",").mapNotNull { it.toIntOrNull() }
            if(repeatDays.isNotEmpty()){
                // Check if already scheduled
                val existingNext = todoDao.findTodoByPrevTodoId(getTodo.id)
                if (existingNext == null) {
                    val currentDate = LocalDate.parse(getTodo.date)
                    var nextDate = currentDate.plusDays(1)

                    while (!repeatDays.contains(nextDate.dayOfWeek.value)) {
                        nextDate = nextDate.plusDays(1)
                    }

                    val newTodo = getTodo.copy(
                        id = generateUniqueId(),
                        date = nextDate.toString(),
                        status = TodoStatus.PENDING,
                        prevTodoId = getTodo.id
                    )
                    val newId = todoDao.addTodo(newTodo)

                    // Compute trigger time in millis
                    val localTime = LocalTime.parse(getTodo.time) // "HH:mm"
                    val dateTime = LocalDateTime.of(nextDate, localTime)
                    val triggerMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    // Schedule notification
                    scheduleNotification(
                        context,
                        newTodo.title,
                        "Tap to open",
                        triggerMillis,
                        newTodo
                    )

                    Log.d("TodoWorker", "Created new todo for ${newTodo.date} with id: $newId")
                }
            }
        }
    }
}