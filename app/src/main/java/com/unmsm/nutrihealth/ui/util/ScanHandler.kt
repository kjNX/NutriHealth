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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.unmsm.nutrihealth.data.model.*
import com.unmsm.nutrihealth.data.repository.FoodPredictionService
import com.unmsm.nutrihealth.logic.FoodViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun CameraOrGalleryPicker(
    context: Context,
    foodPredictionService: FoodPredictionService,
    onImageProcessed: (FoodPrediction) -> Unit,
    onError: (String) -> Unit,
    viewModel: FoodViewModel
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
                val body = MultipartBody.Part.createFormData("image", fileToUse.name, requestFile)

                val response = foodPredictionService.predictFood(body)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        currentPrediction = response.body()!!
                        showSaveDialog = true
                    } else {
                        onError("Error al procesar la imagen: ${response.message()}")
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
                    title = { Text("Seleccionar imagen") },
                    text = {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
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
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Tomar Foto")
                            }
                            Button(
                                onClick = {
                                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                                    } else {
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                    requestPermissionWithRationale(permissions, "gallery")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Elegir de la Galería")
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
            title = { Text("Guardar alimento") },
            text = {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DropdownMenuItem(
                                text = { Text("General: ${currentPrediction?.plato_general?.nombre ?: ""}") },
                                onClick = {
                                    selectedPlateIndex = 0
                                    expanded = false
                                }
                            )
                            
                            currentPrediction?.platos_especificos?.forEachIndexed { index, plato ->
                                DropdownMenuItem(
                                    text = { Text("Variante: ${plato.nombre_preparacion}") },
                                    onClick = {
                                        selectedPlateIndex = index + 1
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Campo para la porción
                    OutlinedTextField(
                        value = portion,
                        onValueChange = { newValue ->
                            // Solo permitir números
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                portion = newValue
                                isPortionError = false
                            }
                        },
                        label = { Text("Porción (gramos)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = isPortionError,
                        supportingText = if (isPortionError) {
                            { Text("Por favor ingrese un valor válido mayor a 0") }
                        } else {
                            { Text("Valores nutricionales por ${portion}g") }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val nutricionInfo = when (selectedPlateIndex) {
                        0 -> currentPrediction?.plato_general?.nutricion
                        else -> currentPrediction?.platos_especificos?.getOrNull(selectedPlateIndex - 1)?.nutricion
                    }

                    nutricionInfo?.let { info ->
                        val multiplier = portion.toFloatOrNull()?.div(100f) ?: 1f
                        Text("Calorías: ${String.format("%.1f", info.energia * multiplier)} kcal")
                        Text("Proteínas: ${String.format("%.1f", info.proteinas * multiplier)}g")
                        Text("Grasas: ${String.format("%.1f", info.grasa * multiplier)}g")
                        Text("Agua: ${String.format("%.1f", info.agua * multiplier)}g")
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
                                        }
                                    }
                                }
                            }
                            showSaveDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showSaveDialog = false }) {
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