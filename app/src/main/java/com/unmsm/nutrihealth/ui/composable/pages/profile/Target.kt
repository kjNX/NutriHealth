package com.unmsm.nutrihealth.ui.composable.pages.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.ui.composable.blocks.BlockItem
import com.unmsm.nutrihealth.ui.composable.blocks.EasyCard
import com.unmsm.nutrihealth.ui.composable.blocks.InlineIndicator

@Composable
fun TargetTab(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Goals()
        Spacer(Modifier.height(16.dp))
        Achievements()
    }
}

@Composable
fun Goals() {
    EasyCard(title = "Mi objetivo de peso") {
        InlineIndicator(Icons.Default.Start, "Peso actual", "68kg")
        Spacer(Modifier.height(8.dp))
        InlineIndicator(Icons.Default.ControlPointDuplicate, "Peso objetivo", "62kg")
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Progreso")
            Text(text = "75%")
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator({.75f}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun Achievements() {
    EasyCard(title = "Próximos hitos") {
        BlockItem(
            title = "Primer kilo",
            subtitle = "¡Completado!",
            icon = Icons.Default.Start
        ) {}
        BlockItem(
            title = "Mitad del camino",
            subtitle = "¡Completado!",
            icon = Icons.Default.ControlPointDuplicate
        ) {}
        BlockItem(
            title = "Meta final",
            subtitle = "En progreso (75%)",
            icon = Icons.Default.CalendarMonth
        ) {

        }
    }
}
