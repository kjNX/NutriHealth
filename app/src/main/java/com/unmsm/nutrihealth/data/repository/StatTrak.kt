package com.unmsm.nutrihealth.data.repository

import com.unmsm.nutrihealth.data.model.StatTrak

fun trackerStats() = StatTrak(
    time = "01:50:10",
    mileage = 10f,
    cal = 500,
    avgSpeed = 5.3f
)