package com.unmsm.nutrihealth.ui.composable.pages.map

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.logic.ActivityHistoryViewModel
import com.unmsm.nutrihealth.logic.FoodViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: ActivityHistoryViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val runs = remember { mutableStateOf<List<Run>>(emptyList()) }
    val errorMessage = remember { mutableStateOf("") }
    val context = LocalContext.current
    val foodList = foodViewModel.foodList.collectAsState()

    LaunchedEffect(true) {
        historyViewModel.fetchRunsFromFirestore { retrievedRuns, message ->
            runs.value = retrievedRuns.sortedByDescending { it.timestamp }
            if (message.contains("Error")) {
                errorMessage.value = message
            }
        }
        foodViewModel.loadFood()
    }

    if (errorMessage.value.isNotEmpty()) {
        Toast.makeText(context, errorMessage.value, Toast.LENGTH_SHORT).show()
    }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "PE"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SubsectionTopBar(
            title = "Historial",
            onNavigate = { navController.popBackStack() }
        )

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Actividades") },
                icon = { Icon(Icons.Default.DirectionsRun, contentDescription = null) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Alimentos") },
                icon = { Icon(Icons.Default.Restaurant, contentDescription = null) }
            )
        }

        when (selectedTabIndex) {
            0 -> ActivityHistoryContent(runs.value, dateFormatter)
            1 -> FoodHistoryContent(foodList.value, dateFormatter)
        }
    }
}

@Composable
fun ActivityHistoryContent(runs: List<Run>, dateFormatter: SimpleDateFormat) {
    if (runs.isEmpty()) {
        EmptyStateMessage(
            icon = Icons.Default.DirectionsRun,
            title = "¡Comienza tu primera actividad!",
            message = "Aquí podrás ver el historial de tus actividades físicas"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ActivitySummaryCard(runs)
            }

            items(runs) { run ->
                ActivityCard(run = run, dateFormatter = dateFormatter)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun FoodHistoryContent(foods: List<Food>, dateFormatter: SimpleDateFormat) {
    if (foods.isEmpty()) {
        EmptyStateMessage(
            icon = Icons.Default.Restaurant,
            title = "¡Registra tu primera comida!",
            message = "Aquí podrás ver el historial de tus alimentos registrados"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FoodSummaryCard(foods)
            }

            items(foods.sortedByDescending { it.timestamp }) { food ->
                FoodCard(food = food, dateFormatter = dateFormatter)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun EmptyStateMessage(
    icon: ImageVector,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun FoodSummaryCard(foods: List<Food>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen de Alimentos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Restaurant,
                    value = "${foods.size}",
                    unit = "",
                    label = "Alimentos"
                )

                StatItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${foods.sumOf { it.energy }.toInt()}",
                    unit = "kcal",
                    label = "Calorías Totales"
                )

                StatItem(
                    icon = Icons.Default.Science,
                    value = "${foods.sumOf { it.protein }.toInt()}",
                    unit = "g",
                    label = "Proteína Total"
                )
            }
        }
    }
}

@Composable
fun FoodCard(food: Food, dateFormatter: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${food.energy.toInt()} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = dateFormatter.format(food.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FoodStat(
                    icon = Icons.Default.Science,
                    value = String.format("%.1f", food.protein),
                    unit = "g prot"
                )

                FoodStat(
                    icon = Icons.Default.WaterDrop,
                    value = String.format("%.1f", food.water),
                    unit = "g agua"
                )

                FoodStat(
                    icon = Icons.Default.Opacity,
                    value = String.format("%.1f", food.fats),
                    unit = "g grasas"
                )
            }
        }
    }
}

@Composable
fun FoodStat(
    icon: ImageVector,
    value: String,
    unit: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ActivitySummaryCard(runs: List<Run>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen de Actividades",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Timeline,
                    value = "${runs.sumOf { it.distanceInMeters } / 1000}",
                    unit = "km",
                    label = "Distancia Total"
                )

                StatItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${runs.sumOf { it.caloriesBurned }}",
                    unit = "kcal",
                    label = "Calorías Totales"
                )

                StatItem(
                    icon = Icons.Default.Schedule,
                    value = "${runs.size}",
                    unit = "",
                    label = "Actividades"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    unit: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$value$unit",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ActivityCard(run: Run, dateFormatter: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatter.format(run.timestamp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${run.caloriesBurned} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActivityStat(
                    icon = Icons.Default.DirectionsRun,
                    value = String.format("%.2f", run.distanceInMeters / 1000f),
                    unit = "km"
                )

                ActivityStat(
                    icon = Icons.Default.Timer,
                    value = formatDuration(run.durationInMillis),
                    unit = ""
                )

                ActivityStat(
                    icon = Icons.Default.Speed,
                    value = String.format("%.1f", run.avgSpeedInKMH),
                    unit = "km/h"
                )
            }
        }
    }
}

@Composable
fun ActivityStat(
    icon: ImageVector,
    value: String,
    unit: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatDuration(millis: Long): String {
    val minutes = millis / 1000 / 60
    val seconds = (millis / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(navController = rememberNavController())
}
