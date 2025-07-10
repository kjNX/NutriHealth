package com.unmsm.nutrihealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_target")
object UserTarget {
    @PrimaryKey
    var id: Int = 0
    var targetWeight: Double = 0.0
    var priority: Priority = Priority.Health

    // Propiedades requeridas por AuthViewModel
    var dailyCal: Double = 0.0
    var protein: Double = 0.0
    var carbs: Double = 0.0
    var fat: Double = 0.0

    fun reset() {
        id = 0
        targetWeight = 0.0
        dailyCal = 0.0
        protein = 0.0
        carbs = 0.0
        fat = 0.0
    }

    enum class Priority(val description: String) {
        Health("Mejorar salud"),
        Weight("Bajar de peso"),
        Muscle("Ganar músculo")
    }
}