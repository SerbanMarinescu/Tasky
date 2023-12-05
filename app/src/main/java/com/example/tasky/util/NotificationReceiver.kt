package com.example.tasky.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.tasky.R
import com.example.tasky.common.Constants.APP_SCHEME
import com.example.tasky.common.Constants.DEEP_LINK_HANDLER
import com.example.tasky.common.Constants.EVENT_HOST
import com.example.tasky.common.Constants.NOTIFICATION_CHANNEL_ID
import com.example.tasky.common.Constants.REMINDER_HOST
import com.example.tasky.common.Constants.TASK_HOST
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.AgendaItemType.*
import com.example.tasky.feature_agenda.domain.util.JsonSerializer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver: BroadcastReceiver() {

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    @Inject
    lateinit var jsonSerializer: JsonSerializer

    private lateinit var notificationManager: NotificationManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val agendaItem = intent?.getParcelableExtra(ArgumentTypeEnum.ITEM.name, AgendaItem::class.java) ?: return

        showNotification(
            agendaItem = agendaItem,
            itemType = when(agendaItem) {
                is AgendaItem.Event -> EVENT
                is AgendaItem.Reminder -> REMINDER
                is AgendaItem.Task -> TASK
            }
        )
    }

    private fun showNotification(agendaItem: AgendaItem, itemType: AgendaItemType) {

        val activityIntent = Intent().apply {
            putExtra(DEEP_LINK_HANDLER, true)
            action = Intent.ACTION_VIEW
            data = when(itemType) {
                EVENT -> "$APP_SCHEME://$EVENT_HOST?${ArgumentTypeEnum.ITEM_ID.name}=${agendaItem.id}".toUri()
                REMINDER -> "$APP_SCHEME://$REMINDER_HOST?${ArgumentTypeEnum.ITEM_ID.name}=${agendaItem.id}".toUri()
                TASK -> "$APP_SCHEME://$TASK_HOST?${ArgumentTypeEnum.ITEM_ID.name}=${agendaItem.id}".toUri()
            }
        }

        val pendingIntent = TaskStackBuilder.create(appContext).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(
                agendaItem.hashCode(),
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(agendaItem.title)
            .setContentText(agendaItem.description)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(agendaItem.hashCode(), notification)
    }
}