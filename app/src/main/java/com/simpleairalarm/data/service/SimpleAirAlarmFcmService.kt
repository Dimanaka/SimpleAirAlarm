package com.simpleairalarm.data.service

import android.net.Uri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simpleairalarm.data.repository.UserPreferencesRepository
import com.simpleairalarm.domain.model.AlertLevel
import com.simpleairalarm.domain.model.AlertType
import com.simpleairalarm.util.AlarmNotificationManager
import com.simpleairalarm.util.SoundPresetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SimpleAirAlarmFcmService : FirebaseMessagingService() {

    private val userPreferencesRepository by lazy {
        UserPreferencesRepository(applicationContext)
    }

    private val notificationManager by lazy {
        AlarmNotificationManager(applicationContext)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        if (data.isEmpty()) return

        val locationId = data["location_id"] ?: return
        val locationTitle = data["location_title"] ?: "Обраний регіон"
        val alertLevelStr = data["alert_level"]
        val alertTypeStr = data["alert_type"]

        val level = AlertLevel.fromString(alertLevelStr)
        val type = AlertType.fromString(alertTypeStr)

        CoroutineScope(Dispatchers.IO).launch {
            val prefs = userPreferencesRepository.userPreferencesFlow.first()

            // Перевіряємо чи повідомлення стосується обраного регіону
            if (locationId == prefs.selectedRegionId || locationId == "all") {
                val customUriString = when (level) {
                    AlertLevel.RED -> prefs.customRedSoundUri
                    AlertLevel.YELLOW -> prefs.customYellowSoundUri
                    AlertLevel.NONE -> prefs.customClearSoundUri
                }

                val soundUri: Uri? = SoundPresetManager.getSoundUriForLevel(
                    context = applicationContext,
                    level = level,
                    presetId = prefs.soundPresetId,
                    customUriString = customUriString
                )

                notificationManager.showNotification(
                    regionName = if (locationId == "all") "Україна" else prefs.selectedRegionName.ifEmpty { locationTitle },
                    level = level,
                    type = type,
                    customSoundUri = soundUri
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Тут можна зареєструвати токен на сервері за потреби
    }
}
