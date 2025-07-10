package com.unmsm.nutrihealth.data.model

data class FoodPrediction(
    val categoria_detectada: String = "",
    val categoria_general: String = "",
    val plato_general: PlatoGeneral,
    val platos_especificos: List<PlatoEspecifico> = emptyList()
)

data class PlatoGeneral(
    val nombre: String = "",
    val nutricion: Nutricion? = null
)

data class PlatoEspecifico(
    val nombre_preparacion: String = "",
    val nutricion: Nutricion? = null
)

data class Nutricion(
    val agua: Double? = null,
    val energia: Double? = null,
    val grasa: Double? = null,
    val proteinas: Double? = null
)

data class FoodPredictionError(
    val error: String
) 