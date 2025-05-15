package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Face3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(text = "NutriHealth", style = MaterialTheme.typography.titleLarge)
        },
        actions = {
            Row {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Informe detallado"
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Face3,
                        contentDescription = "Perfil"
                    )
                }
            }
        }
    )
}

@Composable
fun NavBar(selectedIdx: Int) {
    val items = listOf("Inicio", "Chat", "Actividades")
    val selectedIcons = listOf(
        Icons.Filled.Home,
        Icons.AutoMirrored.Filled.Chat,
        Icons.Filled.MonitorHeart
    )
    val unselectedIcons = listOf(
        Icons.Outlined.Home,
        Icons.AutoMirrored.Outlined.Chat,
        Icons.Outlined.MonitorHeart
    )

    NavigationBar {
        items.forEachIndexed { idx, item ->
            NavigationBarItem(
                selected = selectedIdx == idx,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = if (selectedIdx == idx) selectedIcons[idx]
                            else unselectedIcons[idx],
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = item)
                }
            )
        }
    }
}

@Preview
@Composable
private fun NavPreview() {
    NutriHealthTheme {
        Scaffold(topBar = { TopBar() }, bottomBar = { NavBar(0) }) { innerPadding ->
            StartDisplay(modifier = Modifier.padding(innerPadding))
        }
    }
}