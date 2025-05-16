package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.repository.getFood
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar

@Composable
fun History(onNavigate: () -> Unit) {
    Scaffold(topBar = { SubsectionTopBar(
        title = "Historial",
        onNavigate = onNavigate
    ) }) { innerPadding ->
        HistoryDisplay(foodHistory = getFood(), modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun HistoryDisplay(foodHistory: List<Food>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(foodHistory) { i ->
            HistoryItem(food = i)
        }
    }
}

@Composable
fun HistoryItem(food: Food, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = food.name, style = MaterialTheme.typography.headlineMedium)
        Column {
            Text(text = "${food.calories} calorías", style = MaterialTheme.typography.labelMedium)
            Text(text = "${food.carbs} carbohidratos", style = MaterialTheme.typography.labelMedium)
            Text(text = "${food.protein} proteínas", style = MaterialTheme.typography.labelMedium)
            Text(text = "${food.fat} grasas", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryPreview() {
    Scaffold(topBar = { SubsectionTopBar(title = "Historial", {}) }) { innerPadding ->
        HistoryDisplay(foodHistory = getFood(), modifier = Modifier.padding(innerPadding))
    }
}