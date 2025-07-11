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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
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
import com.unmsm.nutrihealth.ui.composable.pages.main.filterFoodByDay
import com.unmsm.nutrihealth.ui.composable.pages.main.filterRunByDay

fun getActivityData(
    foodList: List<Food>,
    runList: List<Run>
): Map<String, Map<Int, Float>> {
    val foodHistory = filterFoodByDay(foodList)
    val runHistory = filterRunByDay(runList)
    val dailyAvgSpeed = mutableMapOf<Int, Float>()
    val waterIntake = mutableMapOf<Int, Float>()

    for(i in 0 until foodHistory.size) {
        waterIntake[i] = foodHistory[i].sumOf { it.water }.toFloat()
        if(!runHistory[i].isEmpty()) {
            val distance = runHistory[i].sumOf { it.distanceInMeters }
            if(distance > 0) dailyAvgSpeed[i] = distance.toFloat()
        }
    }
    return mapOf(
        "Distancia recorrida" to dailyAvgSpeed.toMap(),
        "Ingesta de agua" to waterIntake.toMap(),
    )
}

private val LegendLabelKey = ExtraStore.Key<Set<String>>()

@Composable
fun ComboGraph(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    val colors = listOf(Color(0xFF009688), Color(0xFFE91E63))
    val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)

    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer( // Average speed
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(fill = fill(colors[0]), thickness = 16.dp)
                )
            ),
            rememberLineCartesianLayer( // Daily water intake
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(colors[1])))
                )
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
            marker = rememberDefaultCartesianMarker(label = TextComponent()),
            legend = rememberHorizontalLegend(
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
        modifier,
    )
}

@Composable
fun WaterVActivity(historyData: Map<String, Map<Int, Float>>, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val speedData = historyData["Distancia recorrida"]
    val waterData = historyData["Ingesta de agua"]
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(waterData!!.keys, waterData.values) }
            lineSeries { series(speedData!!.keys, speedData.values) }
            extras { extraStore -> extraStore[LegendLabelKey] = historyData.keys }
        }
    }
    ComboGraph(modelProducer, modifier)
}

@Composable
fun ActivityCard(historyData: Map<String, Map<Int, Float>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "📈 Actividad física",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            WaterVActivity(historyData)
        }
    }
}
