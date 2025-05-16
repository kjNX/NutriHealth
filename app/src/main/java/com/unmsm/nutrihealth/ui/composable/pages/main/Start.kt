package com.unmsm.nutrihealth.ui.composable.pages.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.nutrihealth.ui.composable.blocks.BlockItem
import com.unmsm.nutrihealth.ui.composable.blocks.EasyCard

@Composable
fun StartDisplay(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CaloriesCard()
        Spacer(modifier = Modifier.height(16.dp))
        MacronutrientCard()
        Spacer(modifier = Modifier.height(16.dp))
        WaterCard()
        Spacer(modifier = Modifier.height(16.dp))
        StepsCard()
        Spacer(modifier = Modifier.height(16.dp))
        RemindersCard {}
        Spacer(modifier = Modifier.height(16.dp))
        TrendsCard()
    }
}

@Composable
fun CaloriesCard() {
    val animatedProgress by animateFloatAsState(targetValue = 1450f / 2150f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("🔥 Calorías de hoy", style = MaterialTheme.typography.titleMedium)
            Text("1450 de 2150 kcal", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color(0xFFFF7043)
            )
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

@Composable
fun WaterCard() {
    val animatedProgress by animateFloatAsState(targetValue = 1200f / 2500f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("💧 Agua", style = MaterialTheme.typography.titleMedium)
            Text("1200ml / 2500ml", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color(0xFF42A5F5)
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
fun TrendsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("📈 Tendencias", style = MaterialTheme.typography.titleMedium)
            Text("Promedio: 1650 kcal", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            SparklineGraph(data = listOf(1450, 1700, 1600, 1800, 1550))
        }
    }
}

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

@Preview(showBackground = true)
@Composable
private fun StartPreview() {
    StartDisplay()
}
