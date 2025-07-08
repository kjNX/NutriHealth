package com.unmsm.nutrihealth.logic

import com.unmsm.nutrihealth.data.model.UserTarget

data class AccountSetupUiState(
    var genderIndex: Int = 0,
    var intensity: Float = 0f,
    var age: String = "",
    var height: String = "",
    var weight: String = "",
    var targetWeight: String = "",
    var mainGoal: UserTarget.Priority = UserTarget.Priority.Health,
    var tmb: Int = 0,
    var recommendedKcal: Int = 0,
    var protein: Int = 0,
    var carbs: Int = 0,
    var fats: Int = 0,
    var timeToReach: Int = 0
) {}
