package com.unmsm.nutrihealth.ui.composable.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar

@Composable
fun SettingsExport(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = { SubsectionTopBar(title = "Ajustes", onNavigate = onBack) }
    ) { innerPadding ->
        SettingsComposite(
            updateEnabled = uiState.updateEnabled,
            exitEnabled = uiState.exitEnabled,
            name = uiState.name,
            onNameChange = viewModel::updateName,
            email = uiState.email,
            onEmailChange = viewModel::updateEmail,
            password = uiState.password,
            onPasswordChange = viewModel::updatePassword,
            onCommit = viewModel::commitUserChanges,
            onPasswordChangeRequest = { TODO() },
            measureType = uiState.measureType,
            onMeasureTypeToggle = viewModel::toggleMeasureType,
            notifications = uiState.notifications,
            onNotificationsToggle = viewModel::toggleNotifications,
//            onExportRequest = { TODO() },
            onDeleteRequest = { TODO() },
            onLogout = {
                viewModel.logout()
                onLogout()
            },
            modifier = modifier.fillMaxSize().padding(innerPadding)
        )
    }
}
