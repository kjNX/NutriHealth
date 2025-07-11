package com.unmsm.nutrihealth.ui.composable.pages.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.data.model.UserObjective
import com.unmsm.nutrihealth.ui.composable.blocks.BlockItem
import java.util.Calendar
import java.util.Date

private val LegendLabelKey = ExtraStore.Key<Set<String>>()

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

    // Iterate backwards from today for 30 days.
    // We will add the results to the list in reverse order and then reverse the list
    // to get it from oldest to newest.
    for (i in 0 until history) {
        calendar.time = Date() // Reset to current date
        calendar.add(Calendar.DAY_OF_MONTH, -i) // Go back 'i' days
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayToCheck = calendar.time

        // Get the total calories for this day, defaulting to 0 if no entry exists.
        val dailyTotal = dailyFoodMap.getOrDefault(dayToCheck, listOf())
        foodPerDayList.add(dailyTotal)
    }

    // Reverse the list so it's ordered from 30 days ago to today.
    return foodPerDayList.reversed()
}

fun getNutrientsData(foodList: List<Food>) : Map<String, Map<Int, Float>> {
    val history = filterFoodByDay(foodList)
    val proteinHistory = mutableMapOf<Int, Float>()
    val energyHistory = mutableMapOf<Int, Float>()
    val fatsHistory = mutableMapOf<Int, Float>()
    val waterHistory = mutableMapOf<Int, Float>()
    for(i in 0 until history.size) {
        proteinHistory[i] = history[i].sumOf { it.protein }.toFloat()
        energyHistory[i] = history[i].sumOf { it.energy }.toFloat()
        fatsHistory[i] = history[i].sumOf { it.fats }.toFloat()
        waterHistory[i] = history[i].sumOf { it.water }.toFloat()
    }
    return mapOf(
        "Protein" to proteinHistory.toMap(),
        "Energy" to energyHistory.toMap(),
        "Fats" to fatsHistory.toMap(),
        "Water" to waterHistory.toMap()
    )
}

@Composable
fun StartDisplay(
    foodList: List<Food>,
    runList: List<Run>,
    extraWater: Int,
    modifier: Modifier = Modifier
) {
    val todayFood = filterFoodByDay(foodList, 7)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
        TrendsCard(
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
        )
    }
}

@Composable
fun CaloriesCard(aggregateCalories: Int) {
    val animatedProgress by animateFloatAsState(targetValue = aggregateCalories / UserObjective.dailyCal.toFloat())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("🔥 Calorías de hoy", style = MaterialTheme.typography.titleMedium)
            Text("${aggregateCalories} de ${UserObjective.dailyCal} kcal", style = MaterialTheme.typography.bodyLarge)
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
fun WaterCard(waterAmount: Int) {
    val animatedProgress by animateFloatAsState(targetValue = waterAmount / 2500f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("💧 Agua", style = MaterialTheme.typography.titleMedium)
            Text("${waterAmount}ml de 2500ml", style = MaterialTheme.typography.bodyLarge)
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
fun TrendsCard(
    waterHistory: List<Int>,
    energyHistory: List<Int>,
    proteinHistory: List<Int>,
    fatsHistory: List<Int>,
    historyData: Map<String, Map<Int, Float>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "📈 Actividad física de la última semana",
                style = MaterialTheme.typography.titleMedium
            )
//            Text("Promedio: 1650 kcal", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
//            SparklineGraph(data = listOf(1450, 1700, 1600, 1800, 1550))
            WaterVActivity(waterHistory)
            NutrientComparison(historyData)
        }
    }
}

@Composable
fun WaterVActivity(waterHistory: List<Int>, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(waterHistory) }
            lineSeries { series(1450, 1700, 1600, 1800, 1550) }
        }
    }
    ComboGraph(modelProducer, modifier)
}

@Composable
fun NutrientComparison(
    data: Map<String, Map<Int, Float>>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { data.forEach { (_, map) -> series(map.keys, map.values) } }
            extras { extraStore -> extraStore[LegendLabelKey] = data.keys }
        }
    }
    NutrientsLineGraph(modelProducer)
}

@Composable
fun ComboGraph(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(fill = fill(Color(0xffffc002)), thickness = 16.dp)
                )
            ),
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(Color.Blue)))
                )
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer,
        modifier,
    )
}

@Composable
fun NutrientsLineGraph(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    val colors = listOf(Color(0xFFFF7043), Color(0xFF4FC3F7), Color(0xFFE57373), Color(0xFF42A5F5))
    val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    colors.map { color ->
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(color)),
                            areaFill = null,
                            pointProvider =
                                LineCartesianLayer.PointProvider.single(
                                    LineCartesianLayer.point(rememberShapeComponent(fill(color), CorneredShape.Pill))
                                ),
                        )
                    }
                )
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
            marker = rememberDefaultCartesianMarker(label = TextComponent()),
            legend =
                rememberHorizontalLegend(
                    items = { extraStore ->
                        extraStore[LegendLabelKey].forEachIndexed { index, label ->
                            add(
                                LegendItem(
                                    shapeComponent(fill(colors[index]), CorneredShape.Pill),
                                    legendItemLabelComponent,
                                    label,
                                )
                            )
                        }
                    },
                    padding = insets(top = 16.dp),
                )
        ),
        modelProducer,
        modifier.height(300.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
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
    StartDisplay(listOf(), listOf(), 1200)
}
