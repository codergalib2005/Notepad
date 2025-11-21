package com.edureminder.notebook.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.edureminder.notebook.constants.Constants
import com.edureminder.notebook.receiver.RepeatingTasksReceiver
import com.edureminder.notebook.room.todo.Todo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale


fun createNotificationChannel(context: Context){
    val name = "Reminders"
    val desc = "Sends Notifications of the tasks added to the list"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(channelID,name,importance)
    channel.description = desc
    channel.enableLights(true)
    channel.enableVibration(true)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if(notificationManager.getNotificationChannel("Reminder Channel") != null){
        notificationManager.deleteNotificationChannel("Reminder Channel")
    }
    if(notificationManager.notificationChannels.isNullOrEmpty()){
        notificationManager.createNotificationChannel(channel)
    }
}
fun scheduleNotification(
    context: Context,
    titleText: String?,
    messageText: String?,
    triggerAtMillis: Long,
    todo: Todo
) {
    val intent = Intent(context, Notification::class.java).apply {
        putExtra(titleExtra, titleText)
        putExtra(messageExtra, messageText)
        putExtra(notificationIdExtra, todo.id)
        putExtra(notificationTypeExtra, "TODO")
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        todo.notificationID,// safe cast Long -> Int
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
    )
}

fun setRepeatingAlarm(context: Context){
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val tomorrowTimestamp = calendar.timeInMillis
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val intent = Intent(context.applicationContext, RepeatingTasksReceiver::class.java).also {
        it.action = "repeating_tasks"
    }
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        tomorrowTimestamp,
        PendingIntent.getBroadcast(context.applicationContext,
            Constants.BROADCAST_ID,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    )
}

fun scheduleOrUpdateNotification(
    context: Context,
    todo: Todo
) {
    if (todo.date.isEmpty() || todo.time.isEmpty()) return

    val localDate = LocalDate.parse(todo.date)
    val localTime = LocalTime.parse(todo.time)
    val triggerMillis = LocalDateTime.of(localDate, localTime)
        .atZone(ZoneId.systemDefault())
        .toInstant().toEpochMilli()

    scheduleNotification(context, todo.title, "Tap to open", triggerMillis, todo)
}

fun cancelNotification(context: Context, todo: Todo) {
    val intent = Intent(context, Notification::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        todo.notificationID, // ✅ use the same ID you used when scheduling
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(todo.notificationID) // ✅ same notification ID
}


fun getTimeInMillis(date: String): Long {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
    val mDate = sdf.parse(date)
    return mDate!!.time
}