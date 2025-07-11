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
import com.unmsm.nutrihealth.logic.ActivityHistoryViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController, historyViewModel: ActivityHistoryViewModel = viewModel()) {
    val runs = remember { mutableStateOf<List<Run>>(emptyList()) }
    val errorMessage = remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(true) {
        historyViewModel.fetchRunsFromFirestore { retrievedRuns, message ->
            runs.value = retrievedRuns.sortedByDescending { it.timestamp }
            if (message.contains("Error")) {
                errorMessage.value = message
            }
        }
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
            title = "Historial de Actividades",
            onNavigate = { navController.popBackStack() }
        )

        if (runs.value.isEmpty()) {
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
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "¡Comienza tu primera actividad!",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Aquí podrás ver el historial de tus actividades físicas",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ActivitySummaryCard(runs.value)
                }

                items(runs.value) { run ->
                    ActivityCard(run = run, dateFormatter = dateFormatter)
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
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
