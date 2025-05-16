package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.logic.FoodViewModel
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar

@Composable
fun History(
    onNavigate: () -> Unit,
    viewModel: FoodViewModel = viewModel()
) {
    val foodHistory by viewModel.foodList.collectAsState()

    LaunchedEffect(Unit) {
        if (User.id.isNotEmpty()) {
            viewModel.loadFood()
        }
    }

    Scaffold(
        topBar = {
            SubsectionTopBar(
                title = "Historial",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        HistoryDisplay(
            foodHistory = foodHistory,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HistoryDisplay(foodHistory: List<Food>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(foodHistory) { food ->
            HistoryItem(food = food)
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
            Text(text = "${food.fats} grasas", style = MaterialTheme.typography.labelMedium)
        }
    }
}
