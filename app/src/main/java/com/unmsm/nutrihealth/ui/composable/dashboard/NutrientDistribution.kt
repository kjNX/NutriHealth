package com.unmsm.nutrihealth.ui.composable.dashboard

import androidx.compose.runtime.Composable
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.ui.composable.pages.main.filterFoodByDay
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.model.toChartDataSet

val labels = listOf("Calorías", "Proteínas", "Grasas")

fun getAverageDailyConsumption(data: List<Food>) : List<Double> {
    val splitData = filterFoodByDay(data)
    val filteredData = splitData.filter { it.isNotEmpty() }
    val dailyAverage = listOf(
        filteredData.map { it.sumOf { food -> food.energy } / it.size },
        filteredData.map { it.sumOf { food -> food.protein } / it.size },
        filteredData.map { it.sumOf { food -> food.fats } / it.size }
    )
    val dataset = listOf(
        dailyAverage[0].reduce { acc, i -> acc + i },
        dailyAverage[1].reduce { acc, i -> acc + i },
        dailyAverage[2].reduce { acc, i -> acc + i }
    )
    return dataset
}

@Composable
fun NutrientDistribution(data: List<Food>) {
    val dataset = getAverageDailyConsumption(data)
        .toChartDataSet(title = "Distribución nutricional reciente", labels = labels)
    PieChart(dataSet = dataset)
}