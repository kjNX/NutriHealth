package com.unmsm.nutrihealth.data.repository

import com.unmsm.nutrihealth.data.model.Contact

fun getContacts() = listOf(
    Contact("ML", "Tu asistente inteligente"),
    Contact("Dra. Maria Lopez", "Nutricionista"),
    Contact("Carlos Rodriguez", "Entrenador personal")
)