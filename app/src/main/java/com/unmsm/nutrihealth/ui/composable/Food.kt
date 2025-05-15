package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/*
@Composable
fun ScanDisplay(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(96.dp))
        Text(
            text = "Escanea tu comida",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Toma una foto de tu comida para identificarla automáticamente",
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
        Button(onClick = {}) {
            Text(text = "Abrir cámara")
        }
    }
}

@Composable
fun ManualDisplay(dishHistory: List<Dish>, modifier: Modifier = Modifier) {
    var food by remember { mutableStateOf("") }
    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            value = food,
            onValueChange = { food = it },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {}) {
            Text(text = "Añadir alimento")
        }
        DishHistory(dishHistory)
    }
}

@Composable
fun DishCard(dish: Dish, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(10f)) {
                Text(
                    text = dish.food.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${dish.pieces} taza${if (dish.pieces == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Column {
                Text(
                    text = "${(dish.food.calories * dish.pieces) / 1000} kcal",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun DishHistory(history: List<Dish>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Historial reciente", style = MaterialTheme.typography.titleMedium)
        for (i in history) DishCard(dish = i, modifier = Modifier.padding(bottom = 8.dp))
    }
}
*/