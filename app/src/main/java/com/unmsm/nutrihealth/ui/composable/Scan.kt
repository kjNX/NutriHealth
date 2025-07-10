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
import com.unmsm.nutrihealth.logic.FoodViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.util.CameraOrGalleryPicker
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel

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
            foodPredictionService = foodPredictionService,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun ScanDisplay(
    modifier: Modifier = Modifier,
    foodPredictionService: FoodPredictionService,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }
    var foodPrediction by remember { mutableStateOf<FoodPrediction?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAIScan by remember { mutableStateOf(false) }
    val foodViewModel: FoodViewModel = viewModel()

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
                    },
                    viewModel = foodViewModel,
                    onNavigateToHome = onNavigate,
                    isAIScan = isAIScan
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
                EmptyScanPrompt(
                    onScan = { 
                        isAIScan = false
                        showPicker = true 
                    },
                    onScanWithAI = {
                        isAIScan = true
                        showPicker = true
                    }
                )
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Intentar de nuevo")
            }
        }
    )
}

@Composable
fun EmptyScanPrompt(onScan: () -> Unit, onScanWithAI: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Text(
            text = "Escanea tu comida",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Toma una foto de tu plato para obtener información nutricional detallada",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onScan,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Escanear comida")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onScanWithAI,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Escanear comida con IA")
        }
    }
}

@Composable
fun FoodPredictionResult(
    prediction: FoodPrediction,
    onNewScan: () -> Unit,
    foodViewModel: FoodViewModel = viewModel()
) {
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prediction.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        if (prediction.categoria_general.isNotEmpty()) {
            Text(
                text = "Categoría: ${prediction.categoria_general}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Información Nutricional",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val nutricion = prediction.plato_general.nutricion
                Text("Energía: ${String.format("%.1f", nutricion.energia)} kcal")
                Text("Proteínas: ${String.format("%.1f", nutricion.proteinas)}g")
                Text("Grasas: ${String.format("%.1f", nutricion.grasa)}g")
                Text("Agua: ${String.format("%.1f", nutricion.agua)}%")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    isSaving = true
                    foodViewModel.savePredictedFood(
                        Food(
                            name = prediction.name,
                            energy = prediction.plato_general.nutricion.energia,
                            protein = prediction.plato_general.nutricion.proteinas,
                            fats = prediction.plato_general.nutricion.grasa,
                            water = prediction.plato_general.nutricion.agua,
                            timestamp = java.util.Date()
                        )
                    ) { success, message ->
                        isSaving = false
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }

            Button(
                onClick = onNewScan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Nuevo escaneo")
            }
        }
    }
}