package com.unmsm.nutrihealth.data.model

data class LabelFoodPrediction(
    val energy: Double,
    val fats: Double,
    val name: String,
    val protein: Double,
    val water: Double,
    val portion: String
) 