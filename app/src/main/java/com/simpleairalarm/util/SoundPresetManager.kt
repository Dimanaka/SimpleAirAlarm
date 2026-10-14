package com.simpleairalarm.util

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import com.simpleairalarm.domain.model.AlertLevel

data class SoundPreset(
    val id: String,
    val title: String,
    val description: String
)

object SoundPresetManager {

    val presets = listOf(
        SoundPreset(
            id = "classic",
            title = "Класичний",
            description = "Стандартні звуки сповіщень пристрою для тривоги та відбою"
        ),
        SoundPreset(
            id = "minimal",
            title = "Мінімалістичний",
            description = "Короткі тональні сигнали"
        ),
        SoundPreset(
            id = "siren",
            title = "Сирена",
            description = "Гучний звук тривоги"
        )
    )

    fun getSoundUriForLevel(
        context: Context,
        level: AlertLevel,
        presetId: String,
        customUriString: String?
    ): Uri? {
        // Якщо користувач встановив власний звук через системний вибірнік
        if (!customUriString.isNull_or_Empty()) {
            try {
                return Uri.parse(customUriString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Інакше використовуємо системні звуки відповідно до рівня
        return when (level) {
            AlertLevel.RED -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            AlertLevel.YELLOW -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            AlertLevel.NONE -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }
}

private fun String?.isNull_or_Empty(): Boolean = this == null || this.trim().isEmpty()
