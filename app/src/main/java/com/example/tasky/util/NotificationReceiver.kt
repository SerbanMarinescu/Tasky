package com.example.tasky.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.tasky.R
import com.example.tasky.common.Constants.NOTIFICATION_CHANNEL_ID
import com.example.tasky.feature_agenda.domain.model.AgendaItem
import com.example.tasky.feature_agenda.domain.util.AgendaItemType
import com.example.tasky.feature_agenda.domain.util.JsonSerializer
import com.example.tasky.presentation.MainActivity
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
            itemId = agendaItem.id,
            itemTitle = agendaItem.title,
            itemDescription = agendaItem.description,
            itemType = when(agendaItem) {
                is AgendaItem.Event -> AgendaItemType.EVENT
                is AgendaItem.Reminder -> AgendaItemType.REMINDER
                is AgendaItem.Task -> {
                    AgendaItemType.TASK
                }
            }
        )
    }

    private fun showNotification(itemId: String, itemTitle: String, itemDescription: String?, itemType: AgendaItemType) {

        val intent = Intent(appContext, MainActivity::class.java).apply {
            putExtra(ArgumentTypeEnum.TYPE.name, itemType as Parcelable)
            putExtra(ArgumentTypeEnum.ITEM_ID.name, itemId)
        }

        val notification = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.tasky_logo)
            .setBadgeIconType(R.drawable.tasky_logo)
            .setContentTitle(itemTitle)
            .setContentText(itemDescription)
            .setContentIntent(
                PendingIntent.getActivity(
                    appContext,
                    1,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        notificationManager.notify(itemId.hashCode(), notification)
    }
}