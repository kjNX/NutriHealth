package com.unmsm.nutrihealth.ui.composable.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Face3
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    val iconDescription = listOf(
        "Historial",
//        "Informe detallado",
        "Perfil"
    )
    val icons = listOf(
        Icons.Default.History,
//        Icons.Default.Analytics,
        Icons.Default.Face3
    )

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_nutrihealth),
                    contentDescription = null
                )
                Text(text = "NutriHealth", style = MaterialTheme.typography.titleLarge)
            }
        },
        actions = {
            Row {
                for((idx, item) in icons.withIndex())
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = item,
                            contentDescription = iconDescription[idx]
                        )
                    }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubsectionTopBar(title: String) {
    TopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Regresar"
                )
            }
        }
    )
}

@Composable
fun NavBar(pagerState: PagerState) {
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

    var coroutineScope = rememberCoroutineScope()

    NavigationBar {
        items.forEachIndexed { idx, item ->
            NavigationBarItem(
                selected = pagerState.currentPage == idx,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(idx) } },
                icon = {
                    Icon(
                        imageVector = if (pagerState.currentPage == idx) selectedIcons[idx]
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

@Composable
fun EntryFABs() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SmallFloatingActionButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.BorderColor,
                contentDescription = "Ingresar comida"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        FloatingActionButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Escanear comida"
            )
        }
    }
}
/*
@Preview
@Composable
private fun NavPreview() {
    NutriHealthTheme {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { NavBar(0) },
            floatingActionButton = { EntryFABs() }
        ) { innerPadding ->
            StartDisplay(modifier = Modifier.padding(innerPadding))
        }
    }
}
 */