package com.emergencymesh.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emergencymesh.app.data.database.AppDatabase
import com.emergencymesh.app.data.entity.Message
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeState(
    val isRelayEnabled: Boolean = false,
    val connectedPeerCount: Int = 0,
    val isBluetoothEnabled: Boolean = false,
    val messages: List<Message> = emptyList(),
    val nickname: String = "Unknown"
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val messageDao = database.messageDao()
    private val peerDao = database.peerDao()

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUserSettings()
        observeMessages()
        observePeerCount()
    }

    private fun loadUserSettings() {
        val prefs = getApplication<Application>().getSharedPreferences(
            "emergency_mesh_prefs",
            Application.MODE_PRIVATE
        )
        val nickname = prefs.getString("nickname", "Unknown") ?: "Unknown"
        val isRelayEnabled = prefs.getBoolean("relay_enabled", false)
        
        _state.value = _state.value.copy(
            nickname = nickname,
            isRelayEnabled = isRelayEnabled
        )
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageDao.getAllMessages().collect { messages ->
                _state.value = _state.value.copy(messages = messages)
            }
        }
    }

    private fun observePeerCount() {
        viewModelScope.launch {
            peerDao.getConnectedPeerCount().collect { count ->
                _state.value = _state.value.copy(connectedPeerCount = count)
            }
        }
    }

    fun toggleRelay() {
        val newValue = !_state.value.isRelayEnabled
        _state.value = _state.value.copy(isRelayEnabled = newValue)
        
        // Save to preferences
        val prefs = getApplication<Application>().getSharedPreferences(
            "emergency_mesh_prefs",
            Application.MODE_PRIVATE
        )
        prefs.edit().putBoolean("relay_enabled", newValue).apply()
    }

    fun updateBluetoothStatus(enabled: Boolean) {
        _state.value = _state.value.copy(isBluetoothEnabled = enabled)
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val message = Message(
                messageId = java.util.UUID.randomUUID().toString(),
                senderId = "local",
                senderNickname = _state.value.nickname,
                content = content,
                timestamp = System.currentTimeMillis(),
                groupId = null,
                ttl = 5,
                isLocal = true
            )
            messageDao.insertMessage(message)
        }
    }
}
