package com.unmsm.nutrihealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_target")
object UserTarget {
    @PrimaryKey
    var id: Int = 0
    var targetWeight: Float = 0f
    var priority: Priority = Priority.Health

    fun reset() {
        id = 0
        targetWeight = 0f
    }

    enum class Priority(val description: String) {
        Health("Mejorar salud"),
        Weight("Bajar de peso"),
        Muscle("Ganar músculo")
    }
}