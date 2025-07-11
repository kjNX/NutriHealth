package com.unmsm.nutrihealth_app.model.live

// Food define un tipo de comido, importado de /food/ en Firestore
data class Food(
    val name: String = "",
    val type: String = "",
    val category: String = "",
    val energy: Int = 0,
    val protein: Float = 0f,
    val water: Float = 0f,
    val fats: Float = 0f
)
