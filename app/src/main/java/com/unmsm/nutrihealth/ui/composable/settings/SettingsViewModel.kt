package com.unmsm.nutrihealth_app.ui.settings

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

const val MEASURE_TYPE = "measure_type"
const val NOTIFICATIONS = "notifications"
/*
class SettingsViewModel() : ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())
    lateinit var user: User

    init {
        viewModelScope.launch {
            user = databaseRepository.getUser(authRepository.currentSession)!!
            uiState = uiState.copy(name = user.name, email = user.email)
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                measureType = preferencesRepository.getValue(MEASURE_TYPE, false),
                notifications = preferencesRepository.getValue(NOTIFICATIONS, false)
            )
        }
    }

    fun updateName(name: String) { uiState = uiState.copy(name = name, updateEnabled = name != user.name) }
    fun updateEmail(email: String) { uiState = uiState.copy(email = email, updateEnabled = email != user.email) }
//    fun updatePhoneNumber(phoneNumber: String)
//    { if(phoneNumber.all { it.isDigit() }) uiState = uiState.copy(phoneNumber = phoneNumber) }

    suspend fun updateMeasureType()
    { uiState = uiState.copy(measureType = preferencesRepository.getValue(MEASURE_TYPE, false)) }
    suspend fun updateNotifications()
    { uiState = uiState.copy(notifications = preferencesRepository.getValue(NOTIFICATIONS, false)) }

    fun toggleMeasureType(measureType: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setValue(MEASURE_TYPE, measureType)
            updateMeasureType()
        }
    }
    fun toggleNotifications(notifications: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setValue(NOTIFICATIONS, notifications)
            updateNotifications()
        }
    }

    fun commitUserChanges() {
        val updatedName = uiState.name
        val updatedEmail = uiState.email
        var update = user
        viewModelScope.launch {
            uiState = uiState.copy(exitEnabled = false)
            if(updatedName != user.name) update = update.copy(name = updatedName)
            if(updatedEmail != user.email && Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
                update = update.copy(email = updatedEmail)
                authRepository.updateEmail(updatedEmail)
            }
            databaseRepository.setUser(
                uid = authRepository.currentSession,
                user = update
            )
            uiState = uiState.copy(exitEnabled = true)
        }
        user = update
    }

    fun logout() { FirebaseAuth.getInstance().signOut() }
}
*/