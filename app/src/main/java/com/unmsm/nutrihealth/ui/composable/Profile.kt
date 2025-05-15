package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.model.User.MilestoneStatus
import com.unmsm.nutrihealth.ui.composable.blocks.FitCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDisplay(modifier: Modifier = Modifier) {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = "Ana García", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(8.dp))
            Text(text = "ana.garcia@ejemplo.com", style = MaterialTheme.typography.labelSmall)
        }
        PrimaryTabRow(0) {
            Tab(true, onClick = {}) {
                Text(text = "Hello!")
            }
        }
    }
}

@Composable
private fun GoalsTab(user: User) {
    Column(modifier = Modifier.padding(16.dp)) {
        FitCard(title = "Mi objetivo de peso") {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Peso actual", color = Color.Gray)
                    Text("${user.currentWeight} kg", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Peso objetivo", color = Color.Gray)
                    Text("${user.goalWeight} kg", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { user.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Progreso: ${user.progressPercent}%", modifier = Modifier.align(Alignment.End))
        }
        Spacer(modifier = Modifier.height(8.dp))
        FitCard(title = "Próximos hitos") {
            user.milestones.forEachIndexed { index, milestone ->
                MilestoneItem(
                    title = milestone.title,
                    completed = milestone.status == MilestoneStatus.COMPLETED,
                    progress = if (milestone.status == MilestoneStatus.IN_PROGRESS) user.progressPercent else null
                )
//                if (index != user.milestones.lastIndex) Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun MilestoneItem(title: String, completed: Boolean, progress: Int? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (completed) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completado",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(2.dp, Color.Gray, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, modifier = Modifier.weight(1f))

        if (!completed && progress != null) {
            Text("En progreso ($progress%)", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ver", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}
/*
@Composable
private fun PlanTab(nutrition: NutritionPlan, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        FitCard(title = "Plan alimenticio") {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Calorías diarias")
                Text("${nutrition.dailyCalories} kcal", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                NutrientItem("Proteínas", "${(nutrition.proteinGrams * 4 * 100 / nutrition.dailyCalories)}%", "${nutrition.proteinGrams}g")
                NutrientItem("Carbohidratos", "${(nutrition.carbsGrams * 4 * 100 / nutrition.dailyCalories)}%", "${nutrition.carbsGrams}g")
                NutrientItem("Grasas", "${(nutrition.fatGrams * 9 * 100 / nutrition.dailyCalories)}%", "${nutrition.fatGrams}g")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FitCard(title = "Recomendaciones") {
            ClickableItem("Comidas recomendadas", "Basado en tus objetivos") {
                navController.navigate("recommendedMeals")
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ClickableItem("Ejercicios sugeridos", "Para maximizar resultados") {
                navController.navigate("suggestedExercises")
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ClickableItem("Actualizar objetivo", "Modificar tu meta actual") {
                navController.navigate("updateGoal")
            }
        }
    }
}

@Composable
private fun SettingsTab(
    preferences: Preferences,
    onPreferencesChange: (Preferences) -> Unit,
    navController: NavController
) {
    Column(modifier = Modifier.padding(16.dp)) {
        FitCard(title = "Preferencias") {
            SwitchItem("Unidades métricas (kg, cm)", preferences.useMetric) {
                onPreferencesChange(preferences.copy(useMetric = it))
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            SwitchItem("Notificaciones", preferences.notificationsEnabled) {
                onPreferencesChange(preferences.copy(notificationsEnabled = it))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FitCard(title = "Exportar y conectar") {
            ClickableItem("Descargar informe PDF") { /* Acción PDF */ }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ClickableItem("Conectar con Google Fit") {
                navController.navigate("googleFitConnection")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cerrar sesión",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Acción logout */ }
                .padding(16.dp)
        )
    }
}
*/
@Preview(showBackground = true)
@Composable
private fun Preview() {
    ProfileDisplay()
}