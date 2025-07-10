package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.unmsm.nutrihealth.data.model.Food
import androidx.compose.runtime.*
import java.util.Date

@Composable
fun TypeAddDialog(
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (Food) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var energy by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val food = Food(
                    name = name,
                    energy = energy.toDoubleOrNull() ?: 0.0,
                    protein = protein.toDoubleOrNull() ?: 0.0,
                    fats = fat.toDoubleOrNull() ?: 0.0,
                    water = water.toDoubleOrNull() ?: 0.0,
                    timestamp = Date()
                )
                onConfirm(food)
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancelar")
            }
        },
        title = { Text("Agregar comida") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                TextField(value = energy, onValueChange = { energy = it }, label = { Text("Energía (kcal)") })
                TextField(value = protein, onValueChange = { protein = it }, label = { Text("Proteínas (g)") })
                TextField(value = fat, onValueChange = { fat = it }, label = { Text("Grasas (g)") })
                TextField(value = water, onValueChange = { water = it }, label = { Text("Agua (%)") })
            }
        }
    )
}
