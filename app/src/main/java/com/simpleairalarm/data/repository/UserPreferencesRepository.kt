package com.simpleairalarm.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val selectedRegionId: String,
    val selectedRegionName: String,
    val useFcmMode: Boolean,
    val soundPresetId: String,
    val customRedSoundUri: String?,
    val customYellowSoundUri: String?,
    val customClearSoundUri: String?
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val SELECTED_REGION_ID = stringPreferencesKey("selected_region_id")
        val SELECTED_REGION_NAME = stringPreferencesKey("selected_region_name")
        val USE_FCM_MODE = booleanPreferencesKey("use_fcm_mode")
        val SOUND_PRESET_ID = stringPreferencesKey("sound_preset_id")
        val CUSTOM_RED_SOUND_URI = stringPreferencesKey("custom_red_sound_uri")
        val CUSTOM_YELLOW_SOUND_URI = stringPreferencesKey("custom_yellow_sound_uri")
        val CUSTOM_CLEAR_SOUND_URI = stringPreferencesKey("custom_clear_sound_uri")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            selectedRegionId = preferences[Keys.SELECTED_REGION_ID] ?: "11", // За замовчуванням "11" (Київська область)
            selectedRegionName = preferences[Keys.SELECTED_REGION_NAME] ?: "Київська область",
            useFcmMode = preferences[Keys.USE_FCM_MODE] ?: true,
            soundPresetId = preferences[Keys.SOUND_PRESET_ID] ?: "classic",
            customRedSoundUri = preferences[Keys.CUSTOM_RED_SOUND_URI],
            customYellowSoundUri = preferences[Keys.CUSTOM_YELLOW_SOUND_URI],
            customClearSoundUri = preferences[Keys.CUSTOM_CLEAR_SOUND_URI]
        )
    }

    suspend fun updateSelectedRegion(regionId: String, regionName: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SELECTED_REGION_ID] = regionId
            preferences[Keys.SELECTED_REGION_NAME] = regionName
        }
    }

    suspend fun setFcmMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USE_FCM_MODE] = enabled
        }
    }

    suspend fun setSoundPreset(presetId: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SOUND_PRESET_ID] = presetId
        }
    }

    suspend fun setCustomSoundUri(level: String, uriString: String?) {
        context.dataStore.edit { preferences ->
            when (level.uppercase()) {
                "RED" -> {
                    if (uriString != null) preferences[Keys.CUSTOM_RED_SOUND_URI] = uriString
                    else preferences.remove(Keys.CUSTOM_RED_SOUND_URI)
                }
                "YELLOW" -> {
                    if (uriString != null) preferences[Keys.CUSTOM_YELLOW_SOUND_URI] = uriString
                    else preferences.remove(Keys.CUSTOM_YELLOW_SOUND_URI)
                }
                "NONE", "CLEAR" -> {
                    if (uriString != null) preferences[Keys.CUSTOM_CLEAR_SOUND_URI] = uriString
                    else preferences.remove(Keys.CUSTOM_CLEAR_SOUND_URI)
                }
            }
        }
    }
}
