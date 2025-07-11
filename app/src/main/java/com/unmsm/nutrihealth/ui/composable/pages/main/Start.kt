package com.unmsm.nutrihealth.ui.composable.pages.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.ui.composable.dashboard.ActivityCard
import com.unmsm.nutrihealth.ui.composable.dashboard.NutrientDistribution
import com.unmsm.nutrihealth.ui.composable.dashboard.NutrientsCard
import com.unmsm.nutrihealth.ui.composable.dashboard.getActivityData
import com.unmsm.nutrihealth.ui.composable.dashboard.getNutrientsData
import java.util.Calendar
import java.util.Date

fun filterFoodByDay(foodList: List<Food>, history: Int = 7): List<List<Food>> {
    val dailyFoodMap = mutableMapOf<Date, MutableList<Food>>()

    for (food in foodList) {
        val calendar = Calendar.getInstance()
        calendar.time = food.timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        if(!dailyFoodMap.containsKey(startOfDay)) dailyFoodMap[startOfDay] = mutableListOf()
        dailyFoodMap[startOfDay]!!.add(food)
    }

    val foodPerDayList = mutableListOf<List<Food>>()
    val calendar = Calendar.getInstance()

    for (i in 0 until history) {
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -i)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayToCheck = calendar.time

        val dailyTotal = dailyFoodMap.getOrDefault(dayToCheck, listOf())
        foodPerDayList.add(dailyTotal)
    }
    return foodPerDayList.reversed()
}

fun filterRunByDay(runList: List<Run>, history: Int = 7): List<List<Run>> {
    val dailyRunMap = mutableMapOf<Date, MutableList<Run>>()

    for (run in runList) {
        val calendar = Calendar.getInstance()
        calendar.time = run.timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        if(!dailyRunMap.containsKey(startOfDay)) dailyRunMap[startOfDay] = mutableListOf()
        dailyRunMap[startOfDay]!!.add(run)
    }

    val runPerDayList = mutableListOf<List<Run>>()
    val calendar = Calendar.getInstance()

    for (i in 0 until history) {
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -i)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayToCheck = calendar.time

        val dailyTotal = dailyRunMap.getOrDefault(dayToCheck, listOf())
        runPerDayList.add(dailyTotal)
    }
    return runPerDayList.reversed()
}

@Composable
fun NoFoodScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No has escaneado comida",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "¡Comienza a escanear tu comida para obtener información!",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoRunScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No has registrado caminatas")
        Text("¡Comienza a caminar con NutriHealth para obtener información!.")
    }
}

@Composable
fun StartDisplay(
    foodList: List<Food>,
    runList: List<Run>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(foodList.isEmpty()) NoFoodScreen()
        else {
            val todayFood = filterFoodByDay(foodList)
            CaloriesCard(todayFood.last().sumOf { it.energy }.toInt())
//        Spacer(modifier = Modifier.height(16.dp))
//        MacronutrientCard()
//        Spacer(modifier = Modifier.height(16.dp))
            WaterCard(todayFood.last().sumOf { it.water }.toInt())
//        Spacer(modifier = Modifier.height(16.dp))
//        StepsCard()
//        Spacer(modifier = Modifier.height(16.dp))
//        RemindersCard {}
//        Spacer(modifier = Modifier.height(16.dp))
            /*TrendsCard(
                todayFood.map { i: List<Food> ->
                    i.sumOf { it.water }.toInt()
                },
                todayFood.map { i: List<Food> ->
                    i.sumOf { it.energy }.toInt()
                },
                todayFood.map { i: List<Food> ->
                    i.sumOf { it.protein }.toInt()
                },
                todayFood.map { i: List<Food> ->
                    i.sumOf { it.fats }.toInt()
                },
                historyData = getNutrientsData(foodList)
            )*/
            NutrientDistribution(foodList)
            NutrientsCard(historyData = getNutrientsData(foodList))
            if(runList.isEmpty()) NoRunScreen()
            else ActivityCard(historyData = getActivityData(foodList, runList))
        }
    }
}

@Composable
fun CaloriesCard(aggregateCalories: Int) {
    val animatedProgress by animateFloatAsState(targetValue = aggregateCalories / 2500f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("🔥 Calorías de hoy", style = MaterialTheme.typography.titleMedium)
            Text("$aggregateCalories de 2500 kcal", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = { animatedProgress },
                color = Color(0xFFFF7043),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun WaterCard(waterAmount: Int) {
    val animatedProgress by animateFloatAsState(targetValue = waterAmount / 1200f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("💧 Agua", style = MaterialTheme.typography.titleMedium)
            Text("${waterAmount}ml de 1200ml", style = MaterialTheme.typography.bodyLarge)
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

