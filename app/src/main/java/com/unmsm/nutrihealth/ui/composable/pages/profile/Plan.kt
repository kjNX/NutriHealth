package com.unmsm.nutrihealth.ui.composable.pages.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.ui.composable.blocks.BlockItem
import com.unmsm.nutrihealth.ui.composable.blocks.EasyCard

val plan = User.Plan

@Composable
fun PlanTab(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        EasyCard(title = "Plan alimenticio", modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Calorías diarias")
                Text(text = "${plan.dailyCal / 1000f} kcal")
            }
            StatsRow()
        }
        Spacer(Modifier.height(16.dp))
        Recommendations()
    }
}

@Composable
fun StatsRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        ValueCard(
            title = "Proteínas",
            percentage = 30,
            amount = plan.protein,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        ValueCard(
            title = "Carbohidratos",
            percentage = 45,
            amount = plan.carbs,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        ValueCard(
            title = "Grasas",
            percentage = 25,
            amount = plan.fats,
            modifier = Modifier.weight(1f)
        )
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

@Composable
fun ValueCard(
    title: String,
    percentage: Int,
    amount: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = title)
            Text(text = "$percentage%")
            Text(text = "${amount}g")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PlanTab()
}