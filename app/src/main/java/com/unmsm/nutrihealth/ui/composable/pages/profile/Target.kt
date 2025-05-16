package com.unmsm.nutrihealth.ui.composable.pages.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.ui.composable.blocks.BlockItem
import com.unmsm.nutrihealth.ui.composable.blocks.EasyCard
import com.unmsm.nutrihealth.ui.composable.blocks.InlineIndicator

@Composable
fun TargetTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Goals()
        Spacer(Modifier.height(16.dp))
        Achievements()
    }
}

@Composable
fun Goals() {
    val progress = 0.75f
    val animatedProgress by animateFloatAsState(targetValue = progress)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎯 Mi objetivo de peso", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))
            InlineIndicator(Icons.Default.Start, "Peso actual", "68kg")
            Spacer(Modifier.height(8.dp))
            InlineIndicator(Icons.Default.ControlPointDuplicate, "Peso objetivo", "62kg")
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Progreso", style = MaterialTheme.typography.bodyMedium)
                Text(text = "75%", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF66BB6A),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun Achievements() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🏆 Próximos hitos", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            BlockItem(
                title = "Primer kilo",
                subtitle = "¡Completado!",
                icon = Icons.Default.Start
            ) {}

            BlockItem(
                title = "Mitad del camino",
                subtitle = "¡Completado!",
                icon = Icons.Default.ControlPointDuplicate
            ) {}

            BlockItem(
                title = "Meta final",
                subtitle = "En progreso (75%)",
                icon = Icons.Default.CalendarMonth
            ) {}
        }
    }
}
