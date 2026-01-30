package com.emergencymesh.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsState(
    val dataRetentionDays: Int = 7,
    val scanningFrequency: ScanningFrequency = ScanningFrequency.NORMAL,
    val autoDeleteOldMessages: Boolean = true
)

enum class ScanningFrequency(val displayName: String, val intervalMs: Long) {
    LOW("Low (Battery Saver)", 10000),
    NORMAL("Normal", 5000),
    HIGH("High (Faster Discovery)", 2000)
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val prefs = getApplication<Application>().getSharedPreferences(
            "emergency_mesh_prefs",
            Application.MODE_PRIVATE
        )
        
        val dataRetentionDays = prefs.getInt("data_retention_days", 7)
        val scanningFrequencyOrdinal = prefs.getInt("scanning_frequency", ScanningFrequency.NORMAL.ordinal)
        val autoDeleteOldMessages = prefs.getBoolean("auto_delete_old_messages", true)
        
        _state.value = SettingsState(
            dataRetentionDays = dataRetentionDays,
            scanningFrequency = ScanningFrequency.values()[scanningFrequencyOrdinal],
            autoDeleteOldMessages = autoDeleteOldMessages
        )
    }

    fun updateDataRetention(days: Int) {
        _state.value = _state.value.copy(dataRetentionDays = days)
        saveSettings()
    }

    fun updateScanningFrequency(frequency: ScanningFrequency) {
        _state.value = _state.value.copy(scanningFrequency = frequency)
        saveSettings()
    }

    fun updateAutoDelete(enabled: Boolean) {
        _state.value = _state.value.copy(autoDeleteOldMessages = enabled)
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences(
                "emergency_mesh_prefs",
                Application.MODE_PRIVATE
            )
            prefs.edit().apply {
                putInt("data_retention_days", _state.value.dataRetentionDays)
                putInt("scanning_frequency", _state.value.scanningFrequency.ordinal)
                putBoolean("auto_delete_old_messages", _state.value.autoDeleteOldMessages)
                apply()
            }
        }
    }
}
