package com.unmsm.nutrihealth.ui.composable.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.repository.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MEASURE_TYPE = "measure_type"
const val NOTIFICATIONS = "notifications"

data class SettingsUiState(
    val name: String = "",
    val email: String = "",
    val updateEnabled: Boolean = false,
    val exitEnabled: Boolean = true,
    val measureType: Boolean = false,
    val notifications: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())

    init {
        // Initialize with current user data
        uiState = uiState.copy(
            name = User.name,
            email = User.email
        )

        // Load preferences
        viewModelScope.launch {
            try {
                val measureType = preferencesRepository.getValue(MEASURE_TYPE, false)
                val notifications = preferencesRepository.getValue(NOTIFICATIONS, false)
                uiState = uiState.copy(
                    measureType = measureType,
                    notifications = notifications
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateName(name: String) { 
        uiState = uiState.copy(
            name = name, 
            updateEnabled = name != User.name
        ) 
    }

    fun updateEmail(email: String) { 
        uiState = uiState.copy(
            email = email, 
            updateEnabled = email != User.email
        ) 
    }

    fun toggleMeasureType(measureType: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setValue(MEASURE_TYPE, measureType)
                uiState = uiState.copy(measureType = measureType)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleNotifications(notifications: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setValue(NOTIFICATIONS, notifications)
                uiState = uiState.copy(notifications = notifications)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun commitUserChanges() {
        viewModelScope.launch {
            uiState = uiState.copy(exitEnabled = false)

            // Update User object directly since it's a singleton
            User.name = uiState.name
            User.email = uiState.email

            // In a real app, you would update Firebase here

            uiState = uiState.copy(
                updateEnabled = false,
                exitEnabled = true
            )
        }
    }

    fun logout() { 
        FirebaseAuth.getInstance().signOut() 
        User.reset()
    }
}
