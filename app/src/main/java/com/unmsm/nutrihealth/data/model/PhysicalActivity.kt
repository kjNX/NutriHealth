package com.unmsm.nutrihealth.data.model

import kotlin.time.Duration

data class PhysicalActivity(
    val title: String,
    val description: String,
    val time: Duration,
)
