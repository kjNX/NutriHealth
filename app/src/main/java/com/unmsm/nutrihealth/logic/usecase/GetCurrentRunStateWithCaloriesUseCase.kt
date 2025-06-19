package com.unmsm.nutrihealth.logic.usecase


import com.unmsm.nutrihealth.data.model.CurrentRunStateWithCalories
import com.unmsm.nutrihealth.logic.TrackingManager
import com.unmsm.nutrihealth.logic.utils.RunUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class GetCurrentRunStateWithCaloriesUseCase @Inject constructor(
    private val trackingManager: TrackingManager
) {
    // Valor por defecto para el peso del usuario (70 kg)
    private val defaultUserWeightInKg = 70f

    // Función que obtiene el estado de la carrera y calcula las calorías quemadas
    operator fun invoke(): Flow<CurrentRunStateWithCalories> {
        return trackingManager.currentRunState.map { runState ->
            // Usamos el peso por defecto (70 kg) para calcular las calorías quemadas
            val caloriesBurnt = RunUtils.calculateCaloriesBurnt(
                distanceInMeters = runState.distanceInMeters,
                weightInKg = defaultUserWeightInKg  // Peso predeterminado
            ).roundToInt()  // Redondeamos las calorías quemadas a un valor entero

            // Retornamos el estado combinado con las calorías quemadas
            CurrentRunStateWithCalories(
                currentRunState = runState,
                caloriesBurnt = caloriesBurnt
            )
        }
    }
}
