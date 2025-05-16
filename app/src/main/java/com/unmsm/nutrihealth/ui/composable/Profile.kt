package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.composable.pages.profile.PlanTab
import com.unmsm.nutrihealth.ui.composable.pages.profile.SettingsTab
import com.unmsm.nutrihealth.ui.composable.pages.profile.TargetTab
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import kotlinx.coroutines.launch

@Composable
fun Profile(onNavigate: () -> Unit, onLogout: () -> Unit) {
    Scaffold(topBar = { SubsectionTopBar("Perfil", onNavigate = onNavigate) }) { innerPadding ->
        ProfileDisplay(modifier = Modifier.padding(innerPadding), onLogout = onLogout)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDisplay(onLogout: () -> Unit, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()

    val tabLabels = listOf("Objetivos", "Plan", "Ajustes")
    val tabIcons = listOf(Icons.Default.Flag, Icons.Default.FitnessCenter, Icons.Default.Settings)

    Column(modifier = modifier.fillMaxSize()) {

        // 👤 Tarjeta de usuario
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = User.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = User.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 🧭 Pestañas
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            tabLabels.forEachIndexed { idx, label ->
                Tab(
                    selected = idx == pagerState.currentPage,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(idx) }
                    },
                    text = { Text(text = label) },
                    icon = { Icon(tabIcons[idx], contentDescription = null) }
                )
            }
        }

        // 📄 Contenido de cada pestaña
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> TargetTab()
                1 -> PlanTab()
                2 -> SettingsTab(onLogout = onLogout)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NutriHealthTheme {
        Scaffold(topBar = { SubsectionTopBar("Perfil", {}) }) { innerPadding ->
            ProfileDisplay(modifier = Modifier.padding(innerPadding), onLogout = {})
        }
    }
}
