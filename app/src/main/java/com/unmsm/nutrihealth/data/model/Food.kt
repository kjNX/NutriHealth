package com.unmsm.nutrihealth.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Food(
    val name: String = "",
    val energy: Double = 0.0,
    val protein: Double = 0.0,
    val fats: Double = 0.0,
    val water: Double = 0.0,
    val timestamp: Date = Date()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "energy" to energy,
            "protein" to protein,
            "fats" to fats,
            "water" to water,
            "timestamp" to Timestamp(timestamp)
        )
    }
}
