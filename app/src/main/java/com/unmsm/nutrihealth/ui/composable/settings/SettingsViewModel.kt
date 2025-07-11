package com.unmsm.nutrihealth.ui.composable.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.repository.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val MEASURE_TYPE = "measure_type"
const val NOTIFICATIONS = "notifications"

data class SettingsUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val updateEnabled: Boolean = false,
    val exitEnabled: Boolean = true,
    val showDialog: Boolean = false,
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

    fun updatePassword(password: String) {
        uiState = uiState.copy(
            password = password
        )
    }

    fun togglePassDialog() { uiState = uiState.copy(showDialog = !uiState.showDialog) }
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (newPassword != confirmPassword) {
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(exitEnabled = false)

                val user = FirebaseAuth.getInstance().currentUser
                val providerId = user?.providerData?.first()?.providerId

                if (providerId == "password") {
                    val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)
                    user.reauthenticate(credential).await()
                    user.updatePassword(newPassword).await()
                } else Log.d("SettingsViewModel", "Password change not supported for provider: $providerId")

                togglePassDialog()
                uiState = uiState.copy(exitEnabled = true)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error changing password", e)
                uiState = uiState.copy(exitEnabled = true)
            }
        }
    }

    fun toggleMeasureType(measureType: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setValue(MEASURE_TYPE, measureType)
                uiState = uiState.copy(measureType = measureType)
            } catch (e: Exception) {
                Log.d("SettingsViewModel", "toggleMeasureType: ${e.localizedMessage}")
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
            try {
                uiState = uiState.copy(exitEnabled = false)

                // Update User object directly since it's a singleton
                User.name = uiState.name
                User.email = uiState.email

                val user = FirebaseAuth.getInstance().currentUser
                val providerId = user?.providerData?.first()?.providerId

                Log.d("SettingsViewModel", "commitUserChanges: $providerId")

                // Only attempt to update Firebase Auth if the email has changed
                if (user?.email != uiState.email) {
                    // Different authentication based on provider
                    when(providerId) {
                        "password" -> {
                            // For email/password authentication
                            if (uiState.password.isNotEmpty()) {
                                val credential = EmailAuthProvider.getCredential(user.email ?: "", uiState.password)
                                user.reauthenticate(credential).await()
                                user.verifyBeforeUpdateEmail(uiState.email).await()
                            } else {
                                Log.d("SettingsViewModel", "Password required to update email for password provider")
                            }
                        }
                        "google.com" -> {
                            // For Google authentication
                            // Google doesn't support email changes through Firebase directly
                            // We can only update the email in Firestore
                            Log.d("SettingsViewModel", "Email update through Google auth not supported directly")
                        }
                        else -> {
                            Log.d("SettingsViewModel", "Unknown provider: $providerId")
                        }
                    }
                }

                // Update Firestore regardless of authentication provider
                FirebaseFirestore.getInstance().collection("user").document(User.id).set(User).await()

                uiState = uiState.copy(
                    updateEnabled = false,
                    exitEnabled = true
                )
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating user", e)
                uiState = uiState.copy(exitEnabled = true)
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().currentUser?.delete()?.await()
                FirebaseFirestore.getInstance().collection("user").document(User.id).delete().await()
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error deleting account", e)
            }
        }
    }

    fun logout() { 
        FirebaseAuth.getInstance().signOut() 
        User.reset()
    }
}
