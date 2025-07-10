package com.unmsm.nutrihealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_target")
data class UserTarget(
    @PrimaryKey
    var id: Int = 0,
    var targetWeight: Double = 0.0,
    var priority: Priority = Priority.Health,
    var dailyCal: Double = 0.0,
    var protein: Double = 0.0,
    var carbs: Double = 0.0,
    var fat: Double = 0.0
) {
    enum class Priority(val description: String) {
        Health("Mejorar salud"),
        Weight("Bajar de peso"),
        Muscle("Ganar músculo")
    }
}
