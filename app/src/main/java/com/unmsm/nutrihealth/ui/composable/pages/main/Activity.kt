package com.unmsm.nutrihealth.ui.composable.pages.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R
import com.unmsm.nutrihealth.data.model.User

@Composable
fun TrackingDisplay(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.maps),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) { TrackerCard() }
    }
}

@Composable
fun TrackerCard(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column {
                Text(text = User.StatTrak.time, style = MaterialTheme.typography.headlineLarge)
                Text(text = "Completa tu objetivo", style = MaterialTheme.typography.labelSmall)
            }
            Button(onClick = {}) {
                Icon(imageVector = Icons.Default.Pause, contentDescription = "Pausar")
            }
        }
        Row {
            StatCard(
                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                value = User.StatTrak.mileage.toString(),
                measure = "km",
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                value = User.StatTrak.cal.toString(),
                measure = "cal",
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            StatCard(
                icon = Icons.Default.Speed,
                value = User.StatTrak.avgSpeed.toString(),
                measure = "km/h",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/*
@Composable
fun StatRow(stats: StatTrak) {
    Row {
        StatCard(
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
            value = stats.mileage.toString(),
            measure = "km",
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatCard(
            icon = Icons.Default.LocalFireDepartment,
            value = stats.cal.toString(),
            measure = "cal",
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatCard(
            icon = Icons.Default.Speed,
            value = stats.avgSpeed.toString(),
            measure = "km/h",
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun TrainingDisplay(activityList: List<Activity>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(activityList) { i ->
            ActivityCard(i, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun ActivityCard(activity: Activity, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = activity.title, style = MaterialTheme.typography.headlineMedium)
                Button(onClick = {}) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
                }
            }
            Text(
                text = activity.description,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Timelapse, contentDescription = "Time")
                Text(text = activity.time.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

 */

@Composable
fun StatCard(icon: ImageVector, value: String, measure: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
            Text(text = measure, style = MaterialTheme.typography.labelSmall)
        }
    }
}

/*
@Preview
@Composable
private fun ActivityPreview() {
    NutriHealthTheme {
        Scaffold(topBar = { TopBar() }, bottomBar = { NavBar(0) }) { innerPadding ->
            TrackingDisplay(stats = StatTrak("01:30:20", 7f, 382, 12.3f), modifier = Modifier.padding(innerPadding))
        }
    }
}
*/
