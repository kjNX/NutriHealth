package com.unmsm.nutrihealth.ui.composable.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.nutrihealth.ui.composable.blocks.FitCard

@Composable
fun StartDisplay(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        CaloriesCard()
        Spacer(modifier = Modifier.height(16.dp))
        MacronutrientCard()
        Spacer(modifier = Modifier.height(16.dp))
        WaterCard()
        Spacer(modifier = Modifier.height(16.dp))
        StepsCard()
        Spacer(modifier = Modifier.height(16.dp))
        RemindersCard { }
        Spacer(modifier = Modifier.height(16.dp))
        TrendsCard()
    }
}

@Composable
fun RemindersCard(onReminderClick: () -> Unit) {
    FitCard(title = "Recordatorios") {
        Column {
            ReminderItem(
                "Beber 200ml de agua",
                "En 30 minutos",
                Icons.Default.Opacity,
                onClick = onReminderClick
            )
            ReminderItem(
                "Almuerzo programado",
                "En 1 hora y 15 minutos",
                Icons.Default.Restaurant,
                onClick = onReminderClick
            )
        }
    }
}

@Composable
fun CaloriesCard() {
    FitCard(title = "Calorías de hoy") {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text("1450 de 2150 kcal", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = 1450f / 2150f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MacronutrientCard() {
    FitCard(title = "Macronutrientes") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            NutrientProgress("Proteínas", 75f, 161f, MaterialTheme.colorScheme.primary)
            NutrientProgress("Carbos", 120f, 242f, MaterialTheme.colorScheme.tertiary)
            NutrientProgress("Grasas", 40f, 60f, MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun NutrientProgress(name: String, current: Float, target: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(progress = current / target, color = color, strokeWidth = 6.dp)
        Text("${(current / target * 100).toInt()}%", fontSize = 12.sp)
        Text(name, fontSize = 12.sp)
    }
}

@Composable
fun WaterCard() {
    FitCard(title = "Agua") {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text("1200ml / 2500ml", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = 1200f / 2500f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StepsCard() {
    FitCard(title = "Pasos") {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text("6,500 / 10,000", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = 6500f / 10000f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TrendsCard() {
    FitCard(title = "Tendencias") {
        Text("Promedio: 1650 kcal", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ReminderItem(title: String, time: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(time, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Preview
@Composable
private fun StartPreview() {
    StartDisplay()
}
