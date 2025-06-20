package com.unmsm.nutrihealth.data.model

import android.graphics.Bitmap
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.util.Date

data class Run(
    var img: Bitmap? = null,  // La imagen se almacenará en Firebase Storage como un archivo
    var timestamp: Date = Date(),
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var durationInMillis: Long = 0L,
    var caloriesBurned: Int = 0,
    val id: Int = 0
) {
    // Constructor vacío necesario para Firestore
    constructor() : this(
        img = null,
        timestamp = Date(),
        avgSpeedInKMH = 0f,
        distanceInMeters = 0,
        durationInMillis = 0L,
        caloriesBurned = 0,
        id = 0
    )

    // Convierte el objeto `Bitmap` a un `ByteArray` para poder almacenarlo en Firebase Storage
    fun convertImageToByteArray(): ByteArray? {
        img?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
        return null
    }

    // Convierte la fecha de `Date` a `Timestamp` para Firestore
    fun convertDateToTimestamp(): Timestamp {
        return Timestamp(timestamp)
    }
}
