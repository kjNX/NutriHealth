package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.composable.pages.profile.PlanTab
import com.unmsm.nutrihealth.ui.composable.pages.profile.SettingsTab
import com.unmsm.nutrihealth.ui.composable.pages.profile.TargetTab
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import kotlinx.coroutines.launch

@Composable
fun Profile(onNavigate: () -> Unit) {
    Scaffold(topBar = { SubsectionTopBar("Perfil", onNavigate = onNavigate) }) { innerPadding ->
        ProfileDisplay(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDisplay(modifier: Modifier = Modifier) {
    var pagerState = rememberPagerState { 3 }
    var coroutineScope = rememberCoroutineScope()

    val tabLabels = listOf("Objetivos", "Plan", "Ajustes")

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.Bottom) {
            Text(text = "Ana García", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(8.dp))
            Text(text = "ana.garcia@ejemplo.com", style = MaterialTheme.typography.labelSmall)
        }
        PrimaryTabRow(pagerState.currentPage) {
            for((idx, i) in tabLabels.withIndex())
                Tab(
                    selected = idx == pagerState.currentPage,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(idx) } }
                ) { Text(text = i) }
        }
        HorizontalPager(state = pagerState) { page ->
            when(page) {
                0 -> TargetTab()
                1 -> PlanTab()
                2 -> SettingsTab()
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NutriHealthTheme {
        Scaffold(topBar = { SubsectionTopBar("Perfil", {}) }) { innerPadding ->
            ProfileDisplay(modifier = Modifier.padding(innerPadding))
        }
    }
}