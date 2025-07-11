package com.unmsm.nutrihealth.ui.composable.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.nutrihealth.ui.composable.blocks.BlockItem

@Composable
fun SparklineGraph(data: List<Int>) {
    val max = data.maxOrNull()?.toFloat() ?: 1f
    val min = data.minOrNull()?.toFloat() ?: 0f

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(8.dp)
    ) {
        val spacing = size.width / (data.size - 1)
        val height = size.height

        for (i in 0 until data.size - 1) {
            val x1 = spacing * i
            val y1 = height - ((data[i] - min) / (max - min)) * height
            val x2 = spacing * (i + 1)
            val y2 = height - ((data[i + 1] - min) / (max - min)) * height

            drawLine(
                color = Color(0xFF42A5F5),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 4f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.cornerPathEffect(8f)
            )
        }
    }
}

@Composable
fun StepsCard() {
    val animatedProgress by animateFloatAsState(targetValue = 6500f / 10000f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("🚶 Pasos", style = MaterialTheme.typography.titleMedium)
            Text("6,500 / 10,000", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color(0xFF9575CD)
            )
        }
    }
}

@Composable
fun RemindersCard(onReminderClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("⏰ Recordatorios", style = MaterialTheme.typography.titleMedium)
            BlockItem("Beber 200ml de agua", "💧 En 30 minutos", Icons.Default.Opacity, onClick = onReminderClick)
            BlockItem("Almuerzo programado", "🍽 En 1h 15min", Icons.Default.Restaurant, onClick = onReminderClick)
        }
    }
}

@Composable
fun MacronutrientCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("🍽 Macronutrientes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientProgress("🥩 Proteínas", 75f, 161f, Color(0xFF81C784))
                NutrientProgress("🍞 Carbos", 120f, 242f, Color(0xFF4FC3F7))
                NutrientProgress("🧈 Grasas", 40f, 60f, Color(0xFFE57373))
            }
        }
    }
}

@Composable
fun NutrientProgress(name: String, current: Float, target: Float, color: Color) {
    val progress by animateFloatAsState(targetValue = current / target)
    val percent = (progress * 100).toInt()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            progress = progress,
            color = color,
            strokeWidth = 6.dp
        )
        Text("$percent%", fontSize = 14.sp)
        Text(name, fontSize = 12.sp)
    }
}
