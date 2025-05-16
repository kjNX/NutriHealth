package com.unmsm.nutrihealth.data.repository


import com.unmsm.nutrihealth.data.model.Food


fun getFood() = listOf(
    Food("Plátano", calories = 200, protein = 2.5f, carbs = 51f, fats = .7f),
    Food("Naranja", 85, 1.7f, 21f, .2f),
    Food("Manzana", 65, .3f, 17f, .2f),
    Food("Pan", 102, 4.6f, 19f, .9f),
    Food("Queso", 519, 36f, 1.9f, 41f),
    Food("Chocolate", 200, 2f, 27f, 9f),
    Food("Ceviche", 155, 26f, 9f, 1.9f),
    Food("Arrow con pollo", 343, 14f, 55f, 7.2f),
    Food("Sopa de pollo", 86, 6.1f, 8.5f, 2.9f),
    Food("Sprite", 100, 0f, 27f, 0f)
)

