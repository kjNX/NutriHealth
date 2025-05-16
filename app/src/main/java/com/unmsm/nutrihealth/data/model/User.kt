package com.unmsm.nutrihealth.data.model

object User {
    var id: String = ""
    var name: String = ""
    var email: String = ""

    fun reset() {
        id = ""
        name = ""
        email = ""
    }

    object Target {
        var startingWeight: Int = 0
        var currentWeight: Int = 0
        var targetWeight: Int = 0
        var progressPercent: Int = 0

        fun reset() {
            startingWeight = 0
            currentWeight = 0
            targetWeight = 0
            progressPercent = 0
        }

        fun mkRandom() {
            progressPercent = (0..100).random()
            startingWeight = (75..85).random()
            targetWeight = (60..70).random()
            currentWeight = targetWeight + (startingWeight - targetWeight) * (100 - progressPercent) / 100
        }

        fun updatePercentage() {
            progressPercent = startingWeight - currentWeight / startingWeight - targetWeight
        }
    }

    /*
    object Milestones {
        var milestones: List<Milestone> = listOf()
    }
    */

    object Plan {
        var dailyCal: Int = 0
        var protein: Int = 0
        var carbs: Int = 0
        var fats: Int = 0

        fun reset() {
            dailyCal = 0
            protein = 0
            carbs = 0
            fats = 0
        }

        fun mkRandom() {
            dailyCal = (1500..2150).random()
            protein = (100..200).random()
            carbs = (200..300).random()
            fats = (50..120).random()
        }
    }

    object Preferences {
        var useMetric: Boolean = false
        var notificationsEnabled: Boolean = false
    }

    object StatTrak {
        var time: Int = 0
        var mileage: Float = 0f
        var cal: Int = 0
        var avgSpeed: Float = 0f

        fun reset() {
            time = 0
            mileage = 0f
            cal = 0
            avgSpeed = 0f
        }

        fun mkRandom() {
            time = (1600..10000).random()
            mileage = (5..20).random().toFloat()
            cal = time / 10
            avgSpeed = (50..150).random()/10f
        }

        fun twoBitNumber(i: Int): String = when(i) {
            0 -> "00"
            in 1..9 -> "0$i"
            else -> "$i"
        }

        fun parseTime(): String {
            var aux = time
            val hours = (aux / 3600).toInt()
            aux %= 3600
            val minutes = (aux / 60).toInt()
            aux %= 60

            return "${twoBitNumber(hours)}:${twoBitNumber(minutes)}:${twoBitNumber(aux)}"
        }
    }

    /*
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
    */
}
