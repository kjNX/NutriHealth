package com.unmsm.nutrihealth.data.model

object User {
    val name: String = ""
    val email: String = ""
    val currentWeight: Int = 0
    val goalWeight: Int = 0
    val progressPercent: Int = 0

    val milestones: List<Milestone> = listOf()

    object NutritionPlan {
        val dailyCalories: Int = 0
        val proteinGrams: Int = 0
        val carbsGrams: Int = 0
        val fatGrams: Int = 0
    }
    object Preferences {
        val useMetric: Boolean = false
        val notificationsEnabled: Boolean = false
    }

    data class Milestone(
        val title: String,
        val status: MilestoneStatus,
        val description: String = ""
    )

    enum class MilestoneStatus {
        COMPLETED,
        IN_PROGRESS,
        LOCKED
    }
}
