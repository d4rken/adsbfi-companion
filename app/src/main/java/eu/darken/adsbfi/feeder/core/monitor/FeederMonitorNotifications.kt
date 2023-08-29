package eu.darken.adsbfi.feeder.core.monitor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigTextStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbfi.R
import eu.darken.adsbfi.common.BuildConfigWrap
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import eu.darken.adsbfi.common.easterEggProgressMsg
import eu.darken.adsbfi.common.notifications.PendingIntentFlagCompat
import eu.darken.adsbfi.feeder.core.Feeder
import eu.darken.adsbfi.main.ui.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeederMonitorNotifications @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager,
) {

    private val builder: NotificationCompat.Builder

    init {
        NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.feeder_notification_channel_title),
            NotificationManager.IMPORTANCE_DEFAULT
        ).run { notificationManager.createNotificationChannel(this) }

        val openIntent = Intent(context, MainActivity::class.java)
        val openPi = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntentFlagCompat.FLAG_IMMUTABLE
        )

        builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setChannelId(CHANNEL_ID)
            setContentIntent(openPi)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setSmallIcon(R.drawable.ic_chili_alert_24)
            setContentTitle(context.getString(R.string.app_name))
            setContentText(context.getString(easterEggProgressMsg))
        }
    }

    fun notifyOfOfflineDevices(offlineFeeders: Collection<Feeder>) {
        log(TAG) { "notifyOfOfflineDevices($offlineFeeders)" }
        val notification = builder.apply {
            clearActions()
            setContentTitle(context.getString(R.string.feeder_monitor_offline_title))

            val msgText = context.getString(
                R.string.feeder_monitor_offline_message,
                offlineFeeders.joinToString(",") { it.label }
            )
            setContentText(msgText)
            setStyle(BigTextStyle().bigText(msgText))

        }.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun clearOfflineNotifications() {
        log(TAG) { "clearOfflineNotifications()" }
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        val TAG = logTag("Feeder", "Monitor", "Notifications")
        private val CHANNEL_ID = "${BuildConfigWrap.APPLICATION_ID}.notification.channel.feeder.monitor"
        internal const val NOTIFICATION_ID = 75
    }
}
