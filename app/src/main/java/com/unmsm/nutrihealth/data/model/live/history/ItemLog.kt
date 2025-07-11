package com.unmsm.nutrihealth_app.model.live.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// ItemLog es la interfaz base para registros de eventos.
// Debe ser implementado para generar historiales.
interface ItemLog {
    @Composable
    fun GetItemRow(modifier: Modifier = Modifier)
}