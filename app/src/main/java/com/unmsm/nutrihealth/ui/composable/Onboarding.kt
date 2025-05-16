package com.unmsm.nutrihealth.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import kotlinx.coroutines.launch

// i'm putting everything on the same file because no other file cares about the onboarding

data class OnboardingData(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
)

val onboardingList = listOf(
    OnboardingData(
        title = "Bienvenido a NutriHealth",
        description = "Tu asistente personal para alcanzar tus objetivos de salud y fitness",
        image = R.drawable.logo_nutrihealth
    ),
    OnboardingData(
        title = "Seguimiento completo",
        description = "Registra tu alimentación, actividad física y progreso en un solo lugar",
        image = R.drawable.logo_nutrihealth
    ),
    OnboardingData(
        title = "Personalizado para ti",
        description = "Recibe recomendaciones adaptadas a tus objetivos y preferencias",
        image = R.drawable.logo_nutrihealth

    )
)

@Composable
fun OnboardingPage(onboardingData: OnboardingData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(onboardingData.image),
            contentDescription = null
        )
        Text(
            text = onboardingData.title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = onboardingData.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState { onboardingList.size }
    val coroutineScope = rememberCoroutineScope()
    val isFirstPage = pagerState.currentPage == 0
    val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 👉 Botón SALTAR arriba a la derecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinish) {
                Text("Saltar")
            }
        }

        // 👉 Páginas del onboarding
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            OnboardingPage(onboardingList[page])
        }

        // 👉 Botones de navegación
        Row(
            horizontalArrangement = if (isFirstPage) Arrangement.End else Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isFirstPage) {
                Button(onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }) {
                    Text("Regresar")
                }
            }

            Button(onClick = {
                if (isLastPage) {
                    onFinish()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }) {
                Text(if (isLastPage) "Comenzar" else "Siguiente")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    NutriHealthTheme {
        OnboardingScreen(onFinish = {})
    }
}
