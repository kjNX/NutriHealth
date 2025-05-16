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

@Composable
fun TypeAddDialog(
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (Food) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val food = Food(
                    name = name,
                    calories = calories.toIntOrNull() ?: 0,
                    protein = protein.toFloatOrNull() ?: 0f,
                    carbs = carbs.toFloatOrNull() ?: 0f,
                    fats = fats.toFloatOrNull() ?: 0f
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
                TextField(value = calories, onValueChange = { calories = it }, label = { Text("Calorías") })
                TextField(value = protein, onValueChange = { protein = it }, label = { Text("Proteínas") })
                TextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbohidratos") })
                TextField(value = fats, onValueChange = { fats = it }, label = { Text("Grasas") })
            }
        }
    )
}
