package com.simpleairalarm.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simpleairalarm.data.repository.AlertRepository
import com.simpleairalarm.data.repository.UserPreferences
import com.simpleairalarm.data.repository.UserPreferencesRepository
import com.simpleairalarm.domain.model.AlertLevel
import com.simpleairalarm.domain.model.AlertType
import com.simpleairalarm.domain.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedRegion: Location = Location("11", "Київська область"),
    val regions: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val useFcmMode: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)
    private val alertRepository = AlertRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRegions()
        observePreferences()
    }

    private fun loadRegions() {
        val initialRegions = alertRepository.getUkrainianRegions()
        _uiState.value = _uiState.value.copy(regions = initialRegions)
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collectLatest { prefs ->
                val currentRegions = _uiState.value.regions
                val currentSelected = currentRegions.find { it.id == prefs.selectedRegionId }
                    ?: Location(prefs.selectedRegionId, prefs.selectedRegionName)

                _uiState.value = _uiState.value.copy(
                    selectedRegion = currentSelected,
                    useFcmMode = prefs.useFcmMode
                )
            }
        }
    }

    fun onRegionSelected(location: Location) {
        viewModelScope.launch {
            userPreferencesRepository.updateSelectedRegion(location.id, location.name)
            _uiState.value = _uiState.value.copy(selectedRegion = location)
        }
    }

    fun setFcmMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setFcmMode(enabled)
        }
    }
}
