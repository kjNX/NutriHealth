package com.unmsm.nutrihealth_app.model.live.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.Date

// FoodLog define un evento en el que el usuario ha escaneado su comida
data class FoodLog(
    val foodClass: String,
    val timestamp: Date
) : ItemLog {
    @Composable
    override fun GetItemRow(modifier: Modifier) {

    }
}
