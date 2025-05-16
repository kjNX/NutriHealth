package com.unmsm.nutrihealth.ui.composable

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import com.unmsm.nutrihealth.ui.util.CameraOrGalleryPicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }
    var foodScanned by remember { mutableStateOf<Food?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val fakeFood = Food("Hamburguesa con queso", 520, 28f, 42f, 26f)

    val onImagePicked: (Uri) -> Unit = {
        showPicker = false
        isScanning = true // inicia escaneo

        coroutineScope.launch {
            delay(2000) // simula escaneo
            foodScanned = fakeFood
            isScanning = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF8)),
        contentAlignment = Alignment.Center
    ) {
        when {
            showPicker -> {
                CameraOrGalleryPicker(context, onImagePicked)
            }

            isScanning -> {
                ScanningAnimation()
            }

            foodScanned == null -> {
                EmptyScanPrompt(onScan = { showPicker = true })
            }

            else -> {
                ScannedFoodCard(
                    food = foodScanned!!,
                    onCancel = { foodScanned = null },
                    onAdd = { foodScanned = null }
                )
            }
        }
    }
}
@Composable
fun ScanningAnimation() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF7986CB))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Escaneando imagen...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun EmptyScanPrompt(onScan: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(96.dp)
        )
        Text("Escanea tu comida", style = MaterialTheme.typography.titleLarge)
        Text(
            "Toma una foto de tu comida para identificarla automáticamente",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onScan,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Abrir cámara", color = Color.White)
        }
    }
}

@Composable
fun ScannedFoodCard(
    food: Food,
    onCancel: () -> Unit,
    onAdd: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFFDFDFE), shape = MaterialTheme.shapes.large)
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        // Título con subtítulo
        Text(
            text = food.name,
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF333333)
        )
        Text(
            text = "1 unidad (180g)",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        // Valores con íconos y colores pastel
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            NutrientItem("🔥", "${food.calories}", "kcal", Color(0xFFFFE0B2))
            NutrientItem("🍗", "${food.protein}g", "Proteínas", Color(0xFFDCEDC8))
            NutrientItem("🍞", "${food.carbs}g", "Carbos", Color(0xFFB3E5FC))
            NutrientItem("🧈", "${food.fats}g", "Grasas", Color(0xFFF8BBD0))
        }

        Spacer(Modifier.height(20.dp))

        // Botones redondeados
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9FA8DA)),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Cancelar", color = Color.White)
            }
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7986CB)),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Añadir", color = Color.White)
            }
        }
    }
}


@Composable
fun NutrientItem(icon: String, value: String, label: String, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(bgColor.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(text = icon, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
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