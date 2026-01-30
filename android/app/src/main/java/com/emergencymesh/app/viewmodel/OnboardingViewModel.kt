package com.emergencymesh.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingState(
    val nickname: String = "",
    val createGroup: Boolean = false,
    val joinGroup: Boolean = false,
    val groupId: String = "",
    val isComplete: Boolean = false
)

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun updateNickname(nickname: String) {
        _state.value = _state.value.copy(nickname = nickname)
    }

    fun setCreateGroup(create: Boolean) {
        _state.value = _state.value.copy(
            createGroup = create,
            joinGroup = if (create) false else _state.value.joinGroup
        )
    }

    fun setJoinGroup(join: Boolean) {
        _state.value = _state.value.copy(
            joinGroup = join,
            createGroup = if (join) false else _state.value.createGroup
        )
    }

    fun updateGroupId(groupId: String) {
        _state.value = _state.value.copy(groupId = groupId)
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            // Save nickname and group settings to preferences
            val prefs = getApplication<Application>().getSharedPreferences(
                "emergency_mesh_prefs",
                Application.MODE_PRIVATE
            )
            prefs.edit().apply {
                putString("nickname", _state.value.nickname)
                putBoolean("onboarding_complete", true)
                if (_state.value.createGroup || _state.value.joinGroup) {
                    putString("group_id", _state.value.groupId)
                }
                apply()
            }
            _state.value = _state.value.copy(isComplete = true)
        }
    }
}
