package com.unmsm.nutrihealth.ui.composable.pages.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.logic.utils.DateTimeUtils
import com.unmsm.nutrihealth.R
import com.unmsm.nutrihealth.data.model.CurrentRunStateWithCalories
import com.unmsm.nutrihealth.data.model.CurrentRunState

import com.unmsm.nutrihealth.ui.compose.component.RunningStatsItem
import java.math.RoundingMode

@Composable
fun CurrentRunStatsCard(
    modifier: Modifier = Modifier,
    durationInMillis: Long = 0L,
    runState: CurrentRunStateWithCalories,
    onPlayPauseButtonClick: () -> Unit = {},
    onFinish: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .padding(horizontal = 20.dp)
        ) {
            RunningCardTime(
                modifier = Modifier.weight(1f),
                durationInMillis = durationInMillis,
            )
            TrackingControlButton(
                isRunning = runState.currentRunState.isTracking,
                durationInMillis = durationInMillis,
                onFinish = onFinish,
                onPlayPauseButtonClick = onPlayPauseButtonClick
            )
        }

        RunningStats(runState)
    }
}

@Composable
private fun RunningStats(runState: CurrentRunStateWithCalories) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        RunningStatsItem(
            painter = painterResource(id = R.drawable.running_boy),
            unit = "km",
            value = (runState.currentRunState.distanceInMeters / 1000f).toString()
        )
        VerticalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        RunningStatsItem(
            painter = painterResource(id = R.drawable.fire),
            unit = "kcal",
            value = runState.caloriesBurnt.toString()
        )
        VerticalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        RunningStatsItem(
            painter = painterResource(id = R.drawable.bolt),
            unit = "km/hr",
            value = runState.currentRunState.speedInKMH.toString()
        )
    }
}

@Composable
private fun RunningCardTime(
    modifier: Modifier = Modifier,
    durationInMillis: Long,
) {
    Column(modifier = modifier) {
        Text(
            text = "Running Time",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = DateTimeUtils.getFormattedStopwatchTime(durationInMillis),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun TrackingControlButton(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    durationInMillis: Long,
    onFinish: () -> Unit,
    onPlayPauseButtonClick: () -> Unit
) {
    Row(modifier = modifier) {
        if (!isRunning && durationInMillis > 0) {
            IconButton(
                onClick = onFinish,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_finish),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

        IconButton(
            onClick = onPlayPauseButtonClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (isRunning) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentRunStatsCardPreview() {
    var isRunning by rememberSaveable { mutableStateOf(false) }
    CurrentRunStatsCard(
        durationInMillis = 5400000,
        runState = CurrentRunStateWithCalories(
            currentRunState = CurrentRunState(
                distanceInMeters = 600,
                speedInKMH = (6.935 * 3.6).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP)
                    .toFloat(),
                isTracking = isRunning
            ),
            caloriesBurnt = 532
        ),
        onPlayPauseButtonClick = { isRunning = !isRunning },
        onFinish = {}
    )
}
