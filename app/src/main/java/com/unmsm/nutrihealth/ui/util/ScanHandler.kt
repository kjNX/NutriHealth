package com.unmsm.nutrihealth.ui.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.unmsm.nutrihealth.data.model.*
import com.unmsm.nutrihealth.data.repository.FoodPredictionService
import com.unmsm.nutrihealth.logic.FoodViewModel
import kotlinx.coroutines.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.filled.LocalFireDepartment

import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.vector.ImageVector
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


@Composable
fun CameraOrGalleryPicker(
    context: Context,
    foodPredictionService: FoodPredictionService,
    onImageProcessed: (FoodPrediction) -> Unit,
    onError: (String) -> Unit,
    viewModel: FoodViewModel,
    onNavigateToHome: () -> Unit,
    isAIScan: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    var currentPrediction by remember { mutableStateOf<FoodPrediction?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var selectedPlateIndex by remember { mutableStateOf(0) } // 0 para plato general, 1+ para específicos
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity

    fun checkCameraHardware(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir("Pictures")
        if (!storageDir?.exists()!!) {
            storageDir.mkdirs()
        }
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply {
            photoFile = this
        }
    }

    fun getPhotoFileUri(): Uri {
        val file = createImageFile()
        return FileProvider.getUriForFile(
            context,
            "com.unmsm.nutrihealth.fileprovider",
            file
        )
    }

    suspend fun processImage(uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                val fileToUse = if (uri == photoUri) {
                    photoFile?.takeIf { it.exists() && it.length() > 0 }
                } else {
                    createImageFile().also { tempFile ->
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }.takeIf { it.exists() && it.length() > 0 }
                }

                if (fileToUse == null) {
                    throw Exception("No se pudo acceder al archivo de imagen o el archivo está vacío")
                }

                val requestFile = fileToUse.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    if (isAIScan) "photo" else "image",
                    fileToUse.name,
                    requestFile
                )

                withContext(Dispatchers.Main) {
                    try {
                        if (isAIScan) {
                            val aiResponse = foodPredictionService.predictFoodWithAI(body)
                            if (aiResponse.isSuccessful && aiResponse.body() != null) {
                                val aiPrediction = aiResponse.body()!!
                                // Crear un FoodPrediction a partir de AIFoodPrediction
                                currentPrediction = FoodPrediction(
                                    categoria_detectada = aiPrediction.name,
                                    categoria_general = "",
                                    plato_general = PlatoGeneral(
                                        nombre = aiPrediction.name,
                                        nutricion = NutricionInfo(
                                            energia = aiPrediction.energy.toDouble(),
                                            proteinas = aiPrediction.protein.toDouble(),
                                            grasa = aiPrediction.fats.toDouble(),
                                            agua = aiPrediction.water.toDouble()
                                        )
                                    ),
                                    platos_especificos = emptyList()
                                )
                                showSaveDialog = true
                            } else {
                                onError("Error al procesar la imagen con IA: ${aiResponse.message() ?: "Error desconocido"}")
                            }
                        } else {
                            val regularResponse = foodPredictionService.predictFood(body)
                            if (regularResponse.isSuccessful && regularResponse.body() != null) {
                                currentPrediction = regularResponse.body()
                                showSaveDialog = true
                            } else {
                                onError("Error al procesar la imagen: ${regularResponse.message() ?: "Error desconocido"}")
                            }
                        }
                    } catch (e: Exception) {
                        onError("Error al procesar la respuesta: ${e.message}")
                    }
                }

                if (uri != photoUri) {
                    fileToUse.delete()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error al procesar la imagen: ${e.message}")
                }
            } finally {
                photoFile = null
                photoUri = null
            }
        }
    }

    val cameraResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && photoUri != null) {
            coroutineScope.launch {
                processImage(photoUri!!)
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("No se pudo capturar la imagen")
            }
            photoFile?.delete()
            photoFile = null
            photoUri = null
        }
        showDialog = false
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                processImage(it)
            }
        }
        showDialog = false
    }

    // Launcher para permisos múltiples
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.entries.all { it.value }
        if (allGranted) {
            when {
                permissionsMap.containsKey(Manifest.permission.CAMERA) -> {
                    if (!checkCameraHardware()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Este dispositivo no tiene cámara disponible")
                        }
                        return@rememberLauncherForActivityResult
                    }
                    try {
                        photoUri = getPhotoFileUri()
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        }
                        activity?.let { act ->
                            takePictureIntent.resolveActivity(act.packageManager)?.let {
                                cameraResultLauncher.launch(takePictureIntent)
                            }
                        }
                    } catch (ex: Exception) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Error al iniciar la cámara: ${ex.message}")
                        }
                        photoFile?.delete()
                        photoFile = null
                        photoUri = null
                    }
                }
                permissionsMap.containsKey(Manifest.permission.READ_MEDIA_IMAGES) ||
                        permissionsMap.containsKey(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    galleryLauncher.launch("image/*")
                }
            }
        } else {
            val permanentlyDenied = permissionsMap.any { (permission, granted) ->
                !granted && !(activity?.shouldShowRequestPermissionRationale(permission) ?: true)
            }

            if (permanentlyDenied) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Para usar esta función, necesitas habilitar los permisos en la configuración",
                        actionLabel = "Ir a Configuración",
                        duration = SnackbarDuration.Long
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Se necesitan permisos para continuar",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // Función para solicitar permisos con explicación
    fun requestPermissionWithRationale(permissions: Array<String>, action: String) {
        val shouldShowRationale = permissions.any { permission ->
            activity?.shouldShowRequestPermissionRationale(permission) ?: false
        }

        if (shouldShowRationale) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = when (action) {
                        "camera" -> "Necesitamos acceso a la cámara para tomar fotos de los alimentos"
                        else -> "Necesitamos acceso a la galería para seleccionar fotos de alimentos"
                    },
                    actionLabel = "Permitir",
                    duration = SnackbarDuration.Long
                ).let { result ->
                    if (result == SnackbarResult.ActionPerformed) {
                        multiplePermissionsLauncher.launch(permissions)
                    }
                }
            }
        } else {
            multiplePermissionsLauncher.launch(permissions)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("Seleccionar imagen", style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ElevatedButton(
                                onClick = {
                                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        arrayOf(Manifest.permission.CAMERA)
                                    } else {
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        )
                                    }
                                    requestPermissionWithRationale(permissions, "camera")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tomar Foto", style = MaterialTheme.typography.bodyLarge)
                            }

                            ElevatedButton(
                                onClick = {
                                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                                    } else {
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                    requestPermissionWithRationale(permissions, "gallery")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Elegir de la Galería", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    },
                    confirmButton = { }
                )
            }
        }
    }

    if (showSaveDialog && currentPrediction != null) {
        var portion by remember { mutableStateOf("100") }
        var isPortionError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Guardar alimento", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Selector de plato
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Tipo de plato",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    when (selectedPlateIndex) {
                                        0 -> "Plato General: ${currentPrediction?.plato_general?.nombre ?: ""}"
                                        else -> "Variante: ${currentPrediction?.platos_especificos?.getOrNull(selectedPlateIndex - 1)?.nombre_preparacion ?: ""}"
                                    }
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .heightIn(max = 200.dp)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.height(48.dp)
                                        ) {
                                            Icon(Icons.Default.Restaurant, null)
                                            Text("General: ${currentPrediction?.plato_general?.nombre ?: ""}")
                                        }
                                    },
                                    onClick = {
                                        selectedPlateIndex = 0
                                        expanded = false
                                    }
                                )

                                currentPrediction?.platos_especificos?.forEachIndexed { index, plato ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.height(48.dp)
                                            ) {
                                                Icon(Icons.Default.RestaurantMenu, null)
                                                Text("Variante: ${plato.nombre_preparacion}")
                                            }
                                        },
                                        onClick = {
                                            selectedPlateIndex = index + 1
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Campo de porción
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Porción",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = portion,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                        portion = newValue
                                        isPortionError = false
                                    }
                                },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Scale, null, Modifier.size(16.dp))
                                        Text("Gramos")
                                    }
                                },
                                isError = isPortionError,
                                supportingText = if (isPortionError) {
                                    { Text("Por favor ingrese un valor válido mayor a 0") }
                                } else {
                                    { Text("Valores nutricionales por ${portion}g") }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    // Información nutricional
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Información nutricional",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val nutricionInfo = when (selectedPlateIndex) {
                                0 -> currentPrediction?.plato_general?.nutricion
                                else -> currentPrediction?.platos_especificos?.getOrNull(selectedPlateIndex - 1)?.nutricion
                            }

                            nutricionInfo?.let { info ->
                                val multiplier = portion.toFloatOrNull()?.div(100f) ?: 1f
                                NutritionRow(
                                    icon = Icons.Default.LocalFireDepartment,
                                    label = "Calorías",
                                    value = String.format("%.1f kcal", info.energia * multiplier)
                                )
                                NutritionRow(
                                    icon = Icons.Default.FoodBank,  // Cambiado de Egg a FoodBank
                                    label = "Proteínas",
                                    value = String.format("%.1f g", info.proteinas * multiplier)
                                )
                                NutritionRow(
                                    icon = Icons.Default.Opacity,  // Cambiado de Oil a Opacity
                                    label = "Grasas",
                                    value = String.format("%.1f g", info.grasa * multiplier)
                                )
                                NutritionRow(
                                    icon = Icons.Default.WaterDrop,
                                    label = "Agua",
                                    value = String.format("%.1f g", info.agua * multiplier)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val portionValue = portion.toFloatOrNull() ?: 0f
                        if (portionValue <= 0) {
                            isPortionError = true
                            return@Button
                        }

                        coroutineScope.launch {
                            val selectedPlate = when (selectedPlateIndex) {
                                0 -> currentPrediction?.plato_general
                                else -> currentPrediction?.platos_especificos?.getOrNull(selectedPlateIndex - 1)
                            }

                            selectedPlate?.let { plate ->
                                val nutricionInfo = when (plate) {
                                    is PlatoGeneral -> plate.nutricion
                                    is PlatoEspecifico -> plate.nutricion
                                    else -> null
                                }

                                nutricionInfo?.let { nutricion ->
                                    val multiplier = portionValue / 100f
                                    val food = Food(
                                        name = when (plate) {
                                            is PlatoGeneral -> "${plate.nombre} (${portion}g)"
                                            is PlatoEspecifico -> "${plate.nombre_preparacion} (${portion}g)"
                                            else -> ""
                                        },
                                        energy = nutricion.energia * multiplier,
                                        protein = nutricion.proteinas * multiplier,
                                        fats = nutricion.grasa * multiplier,
                                        water = nutricion.agua * multiplier
                                    )

                                    viewModel.savePredictedFood(food) { success, message ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message)
                                            if (success) {
                                                showSaveDialog = false
                                                onNavigateToHome()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSaveDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        if (!showDialog) {
            showDialog = true
        }
    }
}

@Composable
private fun NutritionRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(label)
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}