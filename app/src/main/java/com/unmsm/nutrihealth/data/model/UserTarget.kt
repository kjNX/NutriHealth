package com.unmsm.nutrihealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_target")
object UserTarget {
    @PrimaryKey
    var id: Int = 0
    var targetWeight: Float = 0f
    var dailyCal: Int = 0
    var protein: Int = 0
    var carbs: Int = 0
    var fats: Int = 0
    var priority: Priority = Priority.Health

    fun reset() {
        id = 0
        targetWeight = 0f
        dailyCal = 0
        protein = 0
        carbs = 0
        fats = 0
    }

    enum class Priority(val description: String) {
        Health("Mejorar salud"),
        Weight("Bajar de peso"),
        Muscle("Ganar músculo")
    }
}