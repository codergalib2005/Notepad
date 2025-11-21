package com.edureminder.notebook.work.note

import android.content.Context
import androidx.work.*
import kotlinx.datetime.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class NotificationType {
    NOTE,
    CHECKLIST
}

fun scheduleExactNoteWorker(
    context: Context,
    uniqueWorkId: String,
    timeString: String,
    selectedDaysString: String,
    date: String
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val targetTime = LocalTime.parse(timeString, formatter)

    val now = ZonedDateTime.now()

    // Convert selectedDaysString to list of ints
    val selectedDays = selectedDaysString.split(",")
        .mapNotNull { it.trim().toIntOrNull() }

    // Get today
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    var nextScheduleDateTime: ZonedDateTime? = null

    if (selectedDays.contains(today)) {
        // Today is valid
        val todayDateTime = now.withHour(targetTime.hour).withMinute(targetTime.minute).withSecond(0).withNano(0)
        if (todayDateTime.isAfter(now)) {
            // Today and time is still in future
            nextScheduleDateTime = todayDateTime
        }
    }

    if (nextScheduleDateTime == null) {
        // Need to find next day
        var daysToAdd = 1
        while (daysToAdd <= 7) {
            val nextDate = now.plusDays(daysToAdd.toLong())
            val nextDayOfWeek = nextDate.dayOfWeek.toCalendarDayOfWeek()
            if (selectedDays.contains(nextDayOfWeek)) {
                nextScheduleDateTime = nextDate.withHour(targetTime.hour).withMinute(targetTime.minute).withSecond(0).withNano(0)
                break
            }
            daysToAdd++
        }
    }

    if (nextScheduleDateTime == null) {
        // Fallback in case no day found
        nextScheduleDateTime = now.plusDays(7).withHour(targetTime.hour).withMinute(targetTime.minute).withSecond(0).withNano(0)
    }

    // Calculate delay
    val delayMillis = Duration.between(now, nextScheduleDateTime).toMillis()

    val data = Data.Builder()
        .putString("NOTE_ID", uniqueWorkId)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<NoteWorker>()
        .setInputData(data)
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .addTag("note_$uniqueWorkId")
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "NoteWorker_$uniqueWorkId",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}

fun DayOfWeek.toCalendarDayOfWeek(): Int {
    return when (this) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
    }
}



fun cancelScheduledExactNoteWorkerIfExists(
    context: Context,
    uniqueWorkId: String
) {
    val workName = "NoteWorker_$uniqueWorkId"
    WorkManager.getInstance(context).cancelUniqueWork(workName)
}

fun cancelScheduledExactNoteWorkersIfExist(
    context: Context,
    uniqueWorkIds: List<String>
) {
    val workManager = WorkManager.getInstance(context)
    for (id in uniqueWorkIds) {
        val workName = "NoteWorker_$id"
        workManager.cancelUniqueWork(workName)
    }
}