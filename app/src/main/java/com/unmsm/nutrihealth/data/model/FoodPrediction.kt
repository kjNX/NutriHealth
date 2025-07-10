package com.unmsm.nutrihealth.data.model

data class FoodPrediction(
    val categoria_detectada: String = "",
    val categoria_general: String = "",
    val plato_general: PlatoGeneral = PlatoGeneral(),
    val platos_especificos: List<PlatoEspecifico> = emptyList()
) {
    val name: String
        get() = categoria_detectada.ifEmpty { "No se pudo detectar la comida" }
}

data class PlatoGeneral(
    val nombre: String = "",
    val nutricion: NutricionInfo = NutricionInfo()
)

data class PlatoEspecifico(
    val nombre_preparacion: String = "",
    val nutricion: NutricionInfo = NutricionInfo()
)

data class NutricionInfo(
    val agua: Double = 0.0,
    val energia: Double = 0.0,
    val grasa: Double = 0.0,
    val proteinas: Double = 0.0
)

data class FoodPredictionError(
    val error: String
) 