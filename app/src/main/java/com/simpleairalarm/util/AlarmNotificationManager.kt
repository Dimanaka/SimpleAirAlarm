package com.simpleairalarm.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.simpleairalarm.MainActivity
import com.simpleairalarm.R
import com.simpleairalarm.domain.model.AlertLevel
import com.simpleairalarm.domain.model.AlertType

class AlarmNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_RED = "channel_air_alarm_red"
        const val CHANNEL_ID_YELLOW = "channel_air_alarm_yellow"
        const val CHANNEL_ID_CLEAR = "channel_air_alarm_clear"

        const val NOTIFICATION_ID_ALERT = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // Канал для червоного рівня (Повітряна тривога)
            val channelRed = NotificationChannel(
                CHANNEL_ID_RED,
                "Повітряна тривога (Червоний)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Сповіщення про початок повітряної тривоги"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), audioAttributes)
            }

            // Канал для жовтого рівня (Підвищена небезпека)
            val channelYellow = NotificationChannel(
                CHANNEL_ID_YELLOW,
                "Підвищена небезпека (Жовтий)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Сповіщення про підвищену загрозу"
                enableVibration(true)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            }

            // Канал для відбою
            val channelClear = NotificationChannel(
                CHANNEL_ID_CLEAR,
                "Відбій тривоги",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Сповіщення про відбій повітряної тривоги"
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            }

            notificationManager.createNotificationChannel(channelRed)
            notificationManager.createNotificationChannel(channelYellow)
            notificationManager.createNotificationChannel(channelClear)
        }
    }

    fun showNotification(
        regionName: String,
        level: AlertLevel,
        type: AlertType,
        customSoundUri: Uri? = null
    ) {
        val (channelId, title, body) = when (level) {
            AlertLevel.RED -> Triple(
                CHANNEL_ID_RED,
                "🔴 Повітряна тривога!",
                "У регіоні $regionName оголошено повітряну тривогу! Прямуйте в укриття."
            )
            AlertLevel.YELLOW -> Triple(
                CHANNEL_ID_YELLOW,
                "🟡 Підвищена загроза!",
                "У регіоні $regionName загроза: ${type.titleUa}."
            )
            AlertLevel.NONE -> Triple(
                CHANNEL_ID_CLEAR,
                "🟢 Відбій тривоги!",
                "У регіоні $regionName оголошено відбій (${type.titleUa})."
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = customSoundUri ?: when (level) {
            AlertLevel.RED -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            AlertLevel.YELLOW, AlertLevel.NONE -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(
                if (level == AlertLevel.NONE) NotificationCompat.PRIORITY_DEFAULT
                else NotificationCompat.PRIORITY_MAX
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)

        notificationManager.notify(NOTIFICATION_ID_ALERT, builder.build())

        // Програвання звуку примусово, якщо встановлено кастомний Uri
        if (customSoundUri != null) {
            playSound(customSoundUri)
        }
    }

    private fun playSound(uri: Uri) {
        try {
            val ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
