package com.unmsm.nutrihealth_app.model.live.stage

data class UserPlan(
    val mbr: Float,
    val energy: Int = 0,
    val protein: Float = 0f,
    val water: Float = 0f,
    val fats: Float = 0f
)
