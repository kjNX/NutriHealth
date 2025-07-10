package com.unmsm.nutrihealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_objective")
data class UserPlan(
    @PrimaryKey var userId: String = "",
    var tmb: Float = 0f,
    var energia: Float = 0f,
    var proteinas: Float = 0f,
    var grasas: Float = 0f,
    var agua: Float = 0f
)