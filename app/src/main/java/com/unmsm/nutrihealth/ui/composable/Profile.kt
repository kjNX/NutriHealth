package com.unmsm.nutrihealth.ui.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.model.UserPlan
import com.unmsm.nutrihealth.data.model.UserTarget
import com.unmsm.nutrihealth.ui.composable.blocks.InlineIndicator
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.composable.pages.profile.PlanTab
import com.unmsm.nutrihealth.ui.composable.pages.profile.Recommendations
import com.unmsm.nutrihealth.ui.composable.pages.profile.SettingsTab
import com.unmsm.nutrihealth.ui.composable.pages.profile.StatsRow
import com.unmsm.nutrihealth.ui.composable.pages.profile.TargetTab
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun Profile(onNavigate: () -> Unit, onLogout: () -> Unit) {
    Scaffold(topBar = {
        SubsectionTopBar("Perfil", onNavigate = onNavigate)
    }) { innerPadding ->
        ProfileDisplay(
            modifier = Modifier.padding(innerPadding),
            onLogout = onLogout
        )
    }
}

@Composable
fun Goals(pesoActual: String, pesoObjetivo: String, progress: Float, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(targetValue = progress)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎯 Mi objetivo de peso", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))
            InlineIndicator(Icons.Default.Start, "Peso actual", "${pesoActual}kg")
            Spacer(Modifier.height(8.dp))
            InlineIndicator(Icons.Default.ControlPointDuplicate, "Peso objetivo", "${pesoObjetivo}kg")
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Progreso", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF66BB6A),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDisplay(onLogout: () -> Unit, modifier: Modifier = Modifier) {
    /*val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()

    val tabLabels = listOf(
        "Objetivos",
        "Plan",
//        "Ajustes"
    )
    val tabIcons = listOf(
        Icons.Default.Flag,
        Icons.Default.FitnessCenter,
//        Icons.Default.Settings
    )*/

    /*val coroutineScope = rememberCoroutineScope()
    var userTarget = UserTarget()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            User = FirebaseFirestore.getInstance()
                .collection("user")
            userTarget = FirebaseFirestore.getInstance()
                .collection("user")
                .document(
                    FirebaseAuth.getInstance()
                        .currentUser
                        ?.uid
                        .toString()
                ).col.get().await().toObject(UserTarget::class.java)!!
        }
    }*/

    Column(modifier = modifier.fillMaxSize()) {

        // 👤 Tarjeta de usuario
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = User.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🍽 Plan alimenticio", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Calorías diarias", style = MaterialTheme.typography.bodyLarge)
                    Text("2150 kcal", style = MaterialTheme.typography.bodyLarge)
                }
                StatsRow()
            }
        }
        Goals(
            pesoActual = "75",
            pesoObjetivo = "100",
            progress = .75f,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        )
    }


        /*// 🧭 Barra de pestañas
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabLabels.forEachIndexed { idx, label ->
                Tab(
                    selected = idx == pagerState.currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(idx)
                        }
                    },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = tabIcons[idx],
                            contentDescription = null
                        )
                    }
                )
            }
        }

        // 📄 Contenido de cada pestaña
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> TargetTab()
                1 -> PlanTab()
//                2 -> SettingsTab(onLogout = onLogout)
            }
        }*/

}

@Preview
@Composable
private fun Preview() {
    NutriHealthTheme {
        Scaffold(topBar = {
            SubsectionTopBar("Perfil", {})
        }) { innerPadding ->
            ProfileDisplay(modifier = Modifier.padding(innerPadding), onLogout = {})
        }
    }
}
