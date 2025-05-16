package com.unmsm.nutrihealth.ui.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

@Composable
fun TypeAddDialog(onDismiss: () -> Unit, onCancel: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text(text = "Aceptar") } },
        dismissButton = { TextButton(onClick = onCancel) { Text(text = "Cancelar") } },
        icon = { Icon(imageVector = Icons.Default.Fastfood, contentDescription = null) },
        title = { Text(text = "Ingrese el nombre del plato") },
        text = { TextField(value = "", onValueChange = {}) }
    )
}
