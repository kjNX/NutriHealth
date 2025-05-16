package com.unmsm.nutrihealth.ui.composable.pages.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.ui.composable.blocks.BlockItem
import com.unmsm.nutrihealth.ui.composable.blocks.EasyCard

@Composable
fun PlanTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        EasyCard(title = "Plan alimenticio", modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Calorías diarias", style = MaterialTheme.typography.bodyLarge)
                Text(text = "2150 kcal", style = MaterialTheme.typography.bodyLarge)
            }
            StatsRow()
        }
        Spacer(Modifier.height(16.dp))
        Recommendations()
    }
}

@Composable
fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ValueCard(
            title = "Proteínas",
            percentage = 30,
            amount = 161,
            color = Color(0xFF79DC0F), // celeste claro
            modifier = Modifier.weight(1f)
        )
        ValueCard(
            title = "Carbohidratos",
            percentage = 45,
            amount = 242,
            color = Color(0xFF2196F3), // amarillo suave
            modifier = Modifier.weight(1f)
        )
        ValueCard(
            title = "Grasas",
            percentage = 25,
            amount = 60,
            color = Color(0xFFEE546B), // rojo rosado claro
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ValueCard(
    title: String,
    percentage: Int,
    amount: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(text = "$percentage%", style = MaterialTheme.typography.headlineSmall)
            Text(text = "${amount}g", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun Recommendations() {
    EasyCard(title = "Recomendaciones") {
        BlockItem(title = "Comidas recomendadas", subtitle = "Basado en tus objetivos") { }
        BlockItem(title = "Ejercicios sugeridos", subtitle = "Para maximizar resultados") { }
        BlockItem(title = "Actualizar objetivo", subtitle = "Modificar tu meta actual") { }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PlanTab()
}
