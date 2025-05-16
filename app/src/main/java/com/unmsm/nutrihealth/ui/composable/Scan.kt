package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

@Composable
fun Scan(onNavigate: () -> Unit) {
    Scaffold(topBar = { SubsectionTopBar(
        title = "Escanear comida",
        onNavigate = onNavigate
    ) }) { innerPadding ->
        ScanDisplay(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ScanDisplay(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.camera_mock),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(32, 32, 32)
                        ), 1640f
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(96.dp)) {
                Icon(
                    imageVector = Icons.Default.MotionPhotosOn,
                    contentDescription = "Tomar foto y escanear",
                    modifier = Modifier.size(96.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    NutriHealthTheme {
        Scaffold(topBar = { SubsectionTopBar(title = "Escanear comida", {}) }) { innerPadding ->
            ScanDisplay(modifier = Modifier.padding(innerPadding))
        }
    }
}