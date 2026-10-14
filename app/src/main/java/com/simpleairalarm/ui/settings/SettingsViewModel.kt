package com.simpleairalarm.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simpleairalarm.data.repository.UserPreferences
import com.simpleairalarm.data.repository.UserPreferencesRepository
import com.simpleairalarm.util.SoundPreset
import com.simpleairalarm.util.SoundPresetManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingsUiState(
    val useFcmMode: Boolean = true,
    val selectedPresetId: String = "classic",
    val availablePresets: List<SoundPreset> = SoundPresetManager.presets,
    val customRedSoundUri: String? = null,
    val customYellowSoundUri: String? = null,
    val customClearSoundUri: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collectLatest { prefs ->
                _uiState.value = SettingsUiState(
                    useFcmMode = prefs.useFcmMode,
                    selectedPresetId = prefs.soundPresetId,
                    customRedSoundUri = prefs.customRedSoundUri,
                    customYellowSoundUri = prefs.customYellowSoundUri,
                    customClearSoundUri = prefs.customClearSoundUri
                )
            }
        }
    }

    fun setFcmMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setFcmMode(enabled)
        }
    }

    fun setSoundPreset(presetId: String) {
        viewModelScope.launch {
            userPreferencesRepository.setSoundPreset(presetId)
        }
    }

    fun setCustomSoundUri(level: String, uriString: String?) {
        viewModelScope.launch {
            userPreferencesRepository.setCustomSoundUri(level, uriString)
        }
    }
}
