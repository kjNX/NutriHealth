package com.unmsm.nutrihealth.ui.composable

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.FoodPrediction
import com.unmsm.nutrihealth.data.repository.FoodPredictionService
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.util.CameraOrGalleryPicker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Composable
fun Scan(
    foodPredictionService: FoodPredictionService,
    onNavigate: () -> Unit
) {
    Scaffold(
        topBar = { 
            SubsectionTopBar(
                title = "Escanear comida",
                onNavigate = onNavigate
            ) 
        }
    ) { innerPadding ->
        ScanDisplay(
            modifier = Modifier.padding(innerPadding),
            foodPredictionService = foodPredictionService
        )
    }
}

@Composable
fun ScanDisplay(
    modifier: Modifier = Modifier,
    foodPredictionService: FoodPredictionService
) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }
    var foodPrediction by remember { mutableStateOf<FoodPrediction?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF8)),
        contentAlignment = Alignment.Center
    ) {
        when {
            showPicker -> {
                CameraOrGalleryPicker(
                    context = context,
                    foodPredictionService = foodPredictionService,
                    onImageProcessed = { prediction ->
                        foodPrediction = prediction
                        isScanning = false
                        showPicker = false
                    },
                    onError = { error ->
                        errorMessage = error
                        isScanning = false
                        showPicker = false
                    }
                )
            }

            isScanning -> {
                ScanningAnimation()
            }

            errorMessage != null -> {
                ErrorMessage(
                    message = errorMessage!!,
                    onDismiss = { 
                        errorMessage = null
                        showPicker = true 
                    }
                )
            }

            foodPrediction == null -> {
                EmptyScanPrompt(onScan = { showPicker = true })
            }

            else -> {
                FoodPredictionResult(
                    prediction = foodPrediction!!,
                    onNewScan = {
                        foodPrediction = null
                        showPicker = true
                    }
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
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Red
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(onClick = onDismiss) {
            Text("Intentar de nuevo")
        }
    }
}

@Composable
fun EmptyScanPrompt(onScan: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
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
fun FoodPredictionResult(
    prediction: FoodPrediction,
    onNewScan: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = prediction.plato_general.nombre,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Información Nutricional",
                    style = MaterialTheme.typography.titleMedium
                )
                prediction.plato_general.nutricion?.let { nutricion ->
                    Text("Energía: ${String.format("%.1f", nutricion.energia ?: 0.0)} kcal")
                    Text("Proteínas: ${String.format("%.2f", nutricion.proteinas ?: 0.0)}g")
                    Text("Grasas: ${String.format("%.2f", nutricion.grasa ?: 0.0)}g")
                    Text("Agua: ${String.format("%.1f", nutricion.agua ?: 0.0)}%")
                } ?: Text("Información nutricional no disponible")
            }
        }

        Button(
            onClick = onNewScan,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Escanear otro plato", color = Color.White)
        }
    }
}