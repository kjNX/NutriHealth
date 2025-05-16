package com.unmsm.nutrihealth.ui.composable.pages.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.ui.composable.blocks.EasyButton
import com.unmsm.nutrihealth.ui.composable.blocks.EasyCard

@Composable
fun SettingsTab(onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Preferences()
        Spacer(Modifier.height(16.dp))
        Export()
        Spacer(Modifier.height(12.dp))
        EasyButton(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = "Cerrar sesión",
            onClick = onLogout
        )
    }
}

@Composable
fun Preferences() {
    EasyCard(title = "Preferencias") {
        LabelSwitch(
            checked = User.Preferences.useMetric,
            label = "Unidades métricas (kg, cm)",
            icon = Icons.Default.Difference,
            onTap = { value -> User.Preferences.useMetric = value }
        )
        LabelSwitch(
            checked = User.Preferences.notificationsEnabled,
            label = "Notificaciones",
            icon = Icons.Default.Notifications,
            onTap = { value -> User.Preferences.notificationsEnabled }
        )
    }
}

@Composable
fun Export() {
    EasyCard(title = "Exportar y conectar") {
        EasyButton(icon = Icons.Default.Download, label = "Descargar informe PDF", onClick = {})
        EasyButton(icon = Icons.Default.Share, label = "Conectar a Google Fit", onClick = {})
    }
}

@Composable
fun LabelSwitch(
    checked: Boolean,
    label: String,
    icon: ImageVector,
    onTap: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = label, modifier = Modifier.padding(start = 8.dp))
        }
        Switch(checked = checked, onCheckedChange = onTap)
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SettingsTab(onLogout = {})
}
