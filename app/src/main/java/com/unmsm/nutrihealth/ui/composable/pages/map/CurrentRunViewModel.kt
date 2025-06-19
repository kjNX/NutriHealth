package com.unmsm.nutrihealth.ui.composable.pages.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unmsm.nutrihealth.di.ApplicationScope
import com.unmsm.nutrihealth.di.IoDispatcher
import com.unmsm.nutrihealth.logic.usecase.GetCurrentRunStateWithCaloriesUseCase
import com.unmsm.nutrihealth.data.model.CurrentRunStateWithCalories
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.data.repository.AppRepository
import com.unmsm.nutrihealth.logic.TrackingManager
import com.unmsm.nutrihealth.logic.ActivityHistoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CurrentRunViewModel @Inject constructor(
    private val trackingManager: TrackingManager,
    private val repository: AppRepository,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    getCurrentRunStateWithCaloriesUseCase: GetCurrentRunStateWithCaloriesUseCase
) : ViewModel() {

    val currentRunStateWithCalories = getCurrentRunStateWithCaloriesUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            CurrentRunStateWithCalories()
        )

    val runningDurationInMillis = trackingManager.trackingDurationInMs

    fun playPauseTracking() {
        if (currentRunStateWithCalories.value.currentRunState.isTracking)
            trackingManager.pauseTracking()
        else trackingManager.startResumeTracking()
    }

    fun finishRun(
        bitmap: Bitmap,
        activityHistoryVM: ActivityHistoryViewModel?,
        context: Context
    ) {
        trackingManager.pauseTracking()

        val run = Run(
            img = bitmap,
            avgSpeedInKMH = currentRunStateWithCalories.value.currentRunState.distanceInMeters
                .toBigDecimal()
                .multiply(3600.toBigDecimal())
                .divide(runningDurationInMillis.value.toBigDecimal(), 2, RoundingMode.HALF_UP)
                .toFloat(),
            distanceInMeters = currentRunStateWithCalories.value.currentRunState.distanceInMeters,
            durationInMillis = runningDurationInMillis.value,
            timestamp = Date(),
            caloriesBurned = currentRunStateWithCalories.value.caloriesBurnt
        )

        // Guardado local
        saveRun(run)

        // Guardado en Firestore
        if (activityHistoryVM != null) {
            activityHistoryVM.saveRunToFirestore(run) { success, msg ->
                Log.d("FirestoreSave", msg)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No se pudo guardar en Firestore", Toast.LENGTH_SHORT).show()
        }

        trackingManager.stop()
    }

    private fun saveRun(run: Run) = appCoroutineScope.launch(ioDispatcher) {
        repository.insertRun(run)
    }
}
