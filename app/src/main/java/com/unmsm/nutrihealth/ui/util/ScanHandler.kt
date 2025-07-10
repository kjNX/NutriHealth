package com.unmsm.nutrihealth.ui.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.unmsm.nutrihealth.data.model.FoodPrediction
import com.unmsm.nutrihealth.data.repository.FoodPredictionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.provider.Settings
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun CameraOrGalleryPicker(
    context: Context,
    foodPredictionService: FoodPredictionService,
    onImageProcessed: (FoodPrediction) -> Unit,
    onError: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }
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
                        onImageProcessed(response.body()!!)
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

    LaunchedEffect(Unit) {
        if (!showDialog) {
            showDialog = true
        }
    }
}