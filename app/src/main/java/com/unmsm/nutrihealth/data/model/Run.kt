package com.unmsm.nutrihealth.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "running_table")
data class Run(
    var img: Bitmap? = null,  // Cambié a null para permitir que sea opcional
    var timestamp: Date = Date(),
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var durationInMillis: Long = 0L,
    var caloriesBurned: Int = 0,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    // Constructor vacío necesario para Firestore o Room (sin parámetros)
    constructor() : this(
        img = null,
        timestamp = Date(),
        avgSpeedInKMH = 0f,
        distanceInMeters = 0,
        durationInMillis = 0L,
        caloriesBurned = 0,
        id = 0
    )
}
