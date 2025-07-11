package com.unmsm.nutrihealth_app.model.live.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.Date

// RunLog define un evento de actividad física (las caminatas ejecutadas)
data class RunLog(
    val timestamp: Date,
    val timeInSeconds: Int,
    val minSpeed: Float,
    val maxSpeed: Float,
    val avgSpeed: Float,
    val distanceMt: Int
) : ItemLog {
    @Composable
    override fun GetItemRow(modifier: Modifier) {

    }
}
