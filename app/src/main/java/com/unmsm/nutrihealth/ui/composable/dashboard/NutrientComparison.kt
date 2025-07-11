package com.unmsm.nutrihealth.ui.composable.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.ui.composable.pages.main.filterFoodByDay

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
        "Proteínas" to proteinHistory.toMap(),
        "Calorías" to energyHistory.toMap(),
        "Grasas" to fatsHistory.toMap(),
        "Agua" to waterHistory.toMap()
    )
}

private val LegendLabelKey = ExtraStore.Key<Set<String>>()

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
    NutrientsLineGraph(modelProducer, modifier)
}

@Composable
fun NutrientsCard(historyData: Map<String, Map<Int, Float>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "📈 Consumo de nutrientes",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            NutrientComparison(historyData)
        }
    }
}