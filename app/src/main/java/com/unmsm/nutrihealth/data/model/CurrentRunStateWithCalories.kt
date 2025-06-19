package com.unmsm.nutrihealth.data.model

data class CurrentRunStateWithCalories(
    val currentRunState: CurrentRunState = CurrentRunState(),
    val caloriesBurnt: Int = 0
)