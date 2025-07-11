package com.unmsm.nutrihealth.ui.composable.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun LabeledSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconLabel(title = title, subtitle = subtitle, icon = icon)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun Preferences(
    measureType: Boolean,
    onMeasureTypeToggle: (Boolean) -> Unit,
    notifications: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TitledCard(title = "Preferencias", modifier = modifier) {
        LabeledSwitch(
            title = "Unidades",
            subtitle = "La aplicación utilizará cm y kg",
            checked = measureType,
            onCheckedChange = onMeasureTypeToggle
        )
        LabeledSwitch(
            title = "Notificaciones",
            subtitle = "Las notificaciones están habilitadas",
            checked = notifications,
            onCheckedChange = onNotificationsToggle
        )
    }
}

@Composable
fun DataChoices(
    exitEnabled: Boolean,
//    onExportRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    TitledCard(
        title = "Datos del usuario",
        modifier = modifier
    ) {
//        LabeledButton(
//            title = "Exportar informe",
//            onClick = onExportRequest,
//            icon = Icons.Default.KeyboardArrowDown,
//            modifier = Modifier.fillMaxWidth()
//        )
        LabeledButton(
            title = "Eliminar cuenta",
            onClick = onDeleteRequest,
            icon = Icons.Default.Delete,
            modifier = Modifier.fillMaxWidth(),
            enabled = exitEnabled
        )
    }
}

@Composable
fun AppliPage(
    exitEnabled: Boolean,
    measureType: Boolean,
    onMeasureTypeToggle: (Boolean) -> Unit,
    notifications: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
//    onExportRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Preferences(
            measureType = measureType,
            onMeasureTypeToggle = onMeasureTypeToggle,
            notifications = notifications,
            onNotificationsToggle = onNotificationsToggle
        )
        DataChoices(
//            onExportRequest = onExportRequest,
            exitEnabled = exitEnabled,
            onDeleteRequest = onDeleteRequest
        )
        LabeledButton(
            title = "Cerrar sesión",
            onClick = onLogout,
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            modifier = Modifier.fillMaxWidth(),
            enabled = exitEnabled
        )
    }
}
