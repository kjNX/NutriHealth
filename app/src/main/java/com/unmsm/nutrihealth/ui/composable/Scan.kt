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
import com.unmsm.nutrihealth.data.model.LabelFoodPrediction
import com.unmsm.nutrihealth.data.repository.FoodPredictionService
import com.unmsm.nutrihealth.logic.FoodViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.util.CameraOrGalleryPicker
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Date
import com.unmsm.nutrihealth.data.model.PlatoGeneral
import com.unmsm.nutrihealth.data.model.PlatoEspecifico
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AutoAwesome

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
    var labelPrediction by remember { mutableStateOf<LabelFoodPrediction?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAIScan by remember { mutableStateOf(false) }
    var isLabelScan by remember { mutableStateOf(false) }
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
                        when {
                            isLabelScan -> labelPrediction = prediction as LabelFoodPrediction
                            isAIScan -> foodPrediction = prediction as FoodPrediction
                            else -> foodPrediction = prediction as FoodPrediction
                        }
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
                    isAIScan = isAIScan,
                    isLabelScan = isLabelScan
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

            foodPrediction == null && labelPrediction == null -> {
                EmptyScanPrompt(
                    onScan = {
                        isAIScan = false
                        isLabelScan = false
                        showPicker = true
                    },
                    onScanWithAI = {
                        isAIScan = true
                        isLabelScan = false
                        showPicker = true
                    },
                    onScanLabel = {
                        isAIScan = false
                        isLabelScan = true
                        showPicker = true
                    }
                )
            }

            labelPrediction != null -> {
                LabelPredictionResult(
                    prediction = labelPrediction!!,
                    onNewScan = {
                        labelPrediction = null
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
fun EmptyScanPrompt(
    onScan: () -> Unit,
    onScanWithAI: () -> Unit,
    onScanLabel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "¿Qué deseas escanear?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Elige el modo de escaneo que mejor se adapte a tu comida",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onScan),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Escaneo Rápido",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Ideal para platos peruanos",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onScanWithAI),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Escaneo Inteligente",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Análisis detallado con IA para platos complejos",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onScanLabel),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Escaneo de Etiquetas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Lee información nutricional de etiquetas de alimentos",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun LabelPredictionResult(
    prediction: LabelFoodPrediction,
    onNewScan: () -> Unit,
    foodViewModel: FoodViewModel = viewModel()
) {
    var isSaving by remember { mutableStateOf(false) }
    var portionPercentage by remember { mutableStateOf(100f) }
    val context = LocalContext.current

    // Calcular valores nutricionales basados en la porción
    val currentEnergy = (prediction.energy * (portionPercentage / 100f))
    val currentProtein = (prediction.protein * (portionPercentage / 100f))
    val currentFats = (prediction.fats * (portionPercentage / 100f))
    val currentWater = (prediction.water * (portionPercentage / 100f))

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

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Información Nutricional",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Porción base y slider
                Text(
                    text = "Porción base: ${prediction.portion}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Ajustar porción: ${portionPercentage.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Slider(
                    value = portionPercentage,
                    onValueChange = { portionPercentage = it },
                    valueRange = 1f..200f,
                    steps = 199,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Valores nutricionales actualizados
                NutritionInfoRow(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "Energía",
                    value = String.format("%.1f kcal", currentEnergy)
                )

                NutritionInfoRow(
                    icon = Icons.Default.Egg,
                    label = "Proteínas",
                    value = String.format("%.1f g", currentProtein)
                )

                NutritionInfoRow(
                    icon = Icons.Default.Opacity,
                    label = "Grasas",
                    value = String.format("%.1f g", currentFats)
                )

                if (prediction.water > 0) {
                    NutritionInfoRow(
                        icon = Icons.Default.WaterDrop,
                        label = "Agua",
                        value = String.format("%.1f g", currentWater)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    isSaving = true
                    foodViewModel.savePredictedFood(
                        Food(
                            name = "${prediction.name} (${portionPercentage.toInt()}%)",
                            energy = currentEnergy,
                            protein = currentProtein,
                            fats = currentFats,
                            water = currentWater,
                            timestamp = Date()
                        )
                    ) { success, message ->
                        isSaving = false
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

@Composable
private fun NutritionInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodPredictionResult(
    prediction: FoodPrediction,
    onNewScan: () -> Unit,
    foodViewModel: FoodViewModel = viewModel()
) {
    var isSaving by remember { mutableStateOf(false) }
    var selectedPlateIndex by remember { mutableStateOf(0) } // 0 para plato general, 1+ para específicos
    var portionPercentage by remember { mutableStateOf(100f) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Obtener el plato seleccionado
    val selectedPlate = when (selectedPlateIndex) {
        0 -> prediction.plato_general
        else -> prediction.platos_especificos.getOrNull(selectedPlateIndex - 1)
    }

    // Calcular valores nutricionales basados en la porción
    val nutricionInfo = when (selectedPlate) {
        is PlatoGeneral -> selectedPlate.nutricion
        is PlatoEspecifico -> selectedPlate.nutricion
        else -> null
    }

    val currentEnergy = nutricionInfo?.energia?.times(portionPercentage / 100f) ?: 0.0
    val currentProtein = nutricionInfo?.proteinas?.times(portionPercentage / 100f) ?: 0.0
    val currentFats = nutricionInfo?.grasa?.times(portionPercentage / 100f) ?: 0.0
    val currentWater = nutricionInfo?.agua?.times(portionPercentage / 100f) ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prediction.categoria_detectada,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de plato
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = when (selectedPlateIndex) {
                    0 -> prediction.plato_general.nombre
                    else -> prediction.platos_especificos[selectedPlateIndex - 1].nombre_preparacion
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Opción para plato general
                DropdownMenuItem(
                    text = { Text(prediction.plato_general.nombre) },
                    onClick = {
                        selectedPlateIndex = 0
                        expanded = false
                    }
                )

                // Opciones para platos específicos
                prediction.platos_especificos.forEachIndexed { index, plato ->
                    DropdownMenuItem(
                        text = { Text(plato.nombre_preparacion) },
                        onClick = {
                            selectedPlateIndex = index + 1
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Información Nutricional",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Ajuste de porción
                Text(
                    text = "Ajustar porción: ${portionPercentage.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Slider(
                    value = portionPercentage,
                    onValueChange = { portionPercentage = it },
                    valueRange = 1f..500f,
                    steps = 199,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Valores nutricionales
                NutritionInfoRow(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "Energía",
                    value = String.format("%.1f kcal", currentEnergy)
                )

                NutritionInfoRow(
                    icon = Icons.Default.Egg,
                    label = "Proteínas",
                    value = String.format("%.1f g", currentProtein)
                )

                NutritionInfoRow(
                    icon = Icons.Default.Opacity,
                    label = "Grasas",
                    value = String.format("%.1f g", currentFats)
                )

                if (currentWater > 0) {
                    NutritionInfoRow(
                        icon = Icons.Default.WaterDrop,
                        label = "Agua",
                        value = String.format("%.1f g", currentWater)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    isSaving = true
                    val plateName = when (selectedPlateIndex) {
                        0 -> prediction.plato_general.nombre
                        else -> prediction.platos_especificos[selectedPlateIndex - 1].nombre_preparacion
                    }
                    foodViewModel.savePredictedFood(
                        Food(
                            name = "$plateName (${portionPercentage.toInt()}%)",
                            energy = currentEnergy,
                            protein = currentProtein,
                            fats = currentFats,
                            water = currentWater,
                            timestamp = Date()
                        )
                    ) { success, message ->
                        isSaving = false
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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