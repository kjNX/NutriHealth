package com.unmsm.nutrihealth.ui.composable.pages.map

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unmsm.nutrihealth.R
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import com.unmsm.nutrihealth.ui.composable.pages.map.component.CurrentRunMap
import com.unmsm.nutrihealth.ui.composable.pages.map.component.CurrentRunStatsCard
import com.unmsm.nutrihealth.ui.util.LocationUtils
import com.unmsm.nutrihealth.ui.util.ComposeUtils
import com.unmsm.nutrihealth.logic.ActivityHistoryViewModel
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
private fun CurrentRunPreview() {
    NutriHealthTheme {
        Surface {
            CurrentRunScreen(rememberNavController())
        }
    }
}

@Composable
fun CurrentRunScreen(
    navController: NavController,
    viewModel: CurrentRunViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activityHistoryVM: ActivityHistoryViewModel = viewModel() // 👈 ViewModel Firestore

    LaunchedEffect(true) {
        LocationUtils.checkAndRequestLocationSetting(context as Activity)
    }

    var isRunningFinished by rememberSaveable { mutableStateOf(false) }
    var shouldShowRunningCard by rememberSaveable { mutableStateOf(false) }

    val runState by viewModel.currentRunStateWithCalories.collectAsStateWithLifecycle()
    val runningDurationInMillis by viewModel.runningDurationInMillis.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(ComposeUtils.slideDownInDuration + 200L)
        shouldShowRunningCard = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa con la ruta de la carrera
        CurrentRunMap(
            pathPoints = runState.currentRunState.pathPoints,
            isRunningFinished = isRunningFinished,
        ) { bitmap ->
            viewModel.finishRun(
                bitmap = bitmap,
                activityHistoryVM = activityHistoryVM,
                context = context
            )
            navController.navigate(com.unmsm.nutrihealth.MainScreen.Main.name) {
                popUpTo(com.unmsm.nutrihealth.MainScreen.Main.name) { inclusive = true }
            }
        }

        // Barra superior
        TopBar(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp),
            onNavigateUp = {
                navController.navigate(com.unmsm.nutrihealth.MainScreen.Main.name) {
                    popUpTo(com.unmsm.nutrihealth.MainScreen.Main.name) { inclusive = true }
                }
            }
        )

        // Botón para ver historial de carreras
        Button(
            onClick = {
                navController.navigate("HistoryScreen") // Aquí navegas a la pantalla de historial
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Ver historial de carreras")
        }

        // Tarjeta con estadísticas de la carrera
        ComposeUtils.SlideUpAnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = shouldShowRunningCard
        ) {
            CurrentRunStatsCard(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                onPlayPauseButtonClick = viewModel::playPauseTracking,
                runState = runState,
                durationInMillis = runningDurationInMillis,
                onFinish = { isRunningFinished = true }
            )
        }
    }
}


@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit
) {
    IconButton(
        onClick = onNavigateUp,
        modifier = modifier
            .size(32.dp)
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                clip = true
            )
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
            contentDescription = "Volver",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
