package com.unmsm.nutrihealth.data.model

object UserObjective {
    var id: Int = 0
    var tmb: Int = 0
    var dailyCal: Int = 0
    var protein: Int = 0
    var carbs: Int = 0
    var fats: Int = 0

    fun reset() {
        id = 0
        tmb = 0
        dailyCal = 0
        protein = 0
        carbs = 0
        fats = 0
    }
}