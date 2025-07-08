package com.unmsm.nutrihealth.data.repository

import com.unmsm.nutrihealth.data.model.Contact
fun getContacts() = listOf(
    Contact("Asesor de Bienestar", "Recomendaciones personalizadas de salud y bienestar"),
    Contact("Nutrición", "Planificación de dieta y asesoría nutricional"),
    Contact("Entrenamiento Personal", "Planes de entrenamiento personalizados")
)
