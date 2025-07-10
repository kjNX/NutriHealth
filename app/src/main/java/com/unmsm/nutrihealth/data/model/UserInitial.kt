package com.unmsm.nutrihealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_data")
data class UserInitial(
    @PrimaryKey var userId: String = "",
    var edad: Int = 0,
    var altura: Float = 0f,
    var pesoInicial: Float = 0f,
    var genero: String = "Male"
)