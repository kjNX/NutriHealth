package com.unmsm.nutrihealth.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun TypeAddDialog(modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = { TextButton(onClick = {}) { Text(text = "Aceptar") } },
        dismissButton = { TextButton(onClick = {}) { Text(text = "Cancelar") } },
        icon = { Icon(imageVector = Icons.Default.Fastfood, contentDescription = null) },
        title = { Text(text = "Ingrese el nombre del plato") },
        text = { TextField(value = "", onValueChange = {}) },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TypeAddDialog()
}