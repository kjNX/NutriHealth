package com.unmsm.nutrihealth.data.model

object User {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    object Target {
        var currentWeight: Int = 0
        var goalWeight: Int = 0
        var progressPercent: Int = 0

    }

    object Milestones {
        var milestones: List<Milestone> = listOf()
    }

    object Plan {
        var dailyCalories: Int = 0
        var proteinGrams: Int = 0
        var carbsGrams: Int = 0
        var fatGrams: Int = 0
    }

    object Preferences {
        var useMetric: Boolean = false
        var notificationsEnabled: Boolean = false
    }

    object StatTrak {
        val time: String = "01:50:10"
        val mileage: Float = 10f
        val cal: Int = 500
        val avgSpeed: Float = 5.3f
    }

    data class Milestone(
        var title: String,
        var status: MilestoneStatus,
        var description: String = ""
    )

    enum class MilestoneStatus {
        COMPLETED,
        IN_PROGRESS,
        LOCKED
    }
}
