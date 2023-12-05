package com.example.tasky.feature_agenda.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.tasky.feature_agenda.data.mapper.toUtcTimestamp
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.NotificationScheduler
import com.example.tasky.util.ArgumentTypeEnum
import com.example.tasky.util.NotificationReceiver
import java.time.LocalDateTime

class NotificationSchedulerImpl(
    private val context: Context,
    private val alarmManager: AlarmManager
): NotificationScheduler {
    override fun scheduleNotification(item: AgendaItem) {

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(ArgumentTypeEnum.ITEM.name, item)
        }

        val currentTime = LocalDateTime.now()

        if(currentTime.isBefore(item.remindAt.toLocalDateTime())) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.remindAt.toUtcTimestamp(),
                PendingIntent.getBroadcast(
                    context,
                    item.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancelNotification(itemId: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                itemId.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}