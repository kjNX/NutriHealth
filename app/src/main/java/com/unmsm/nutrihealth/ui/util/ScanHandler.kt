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

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.CAMERA, false) -> {
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

                    // Verificar que hay una aplicación de cámara disponible
                    activity?.let { act ->
                        takePictureIntent.resolveActivity(act.packageManager)?.let {
                            cameraResultLauncher.launch(takePictureIntent)
                        } ?: run {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("No hay aplicación de cámara disponible")
                            }
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
            permissions.getOrDefault(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                galleryLauncher.launch("image/*")
            }
            else -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Permisos necesarios denegados")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {},
                    title = { Text("Selecciona fuente de imagen") },
                    text = {
                        Column {
                            Button(
                                onClick = {
                                    if (!checkCameraHardware()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Este dispositivo no tiene cámara disponible")
                                        }
                                        return@Button
                                    }
                                    multiplePermissionsLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                Manifest.permission.READ_MEDIA_IMAGES
                                            } else {
                                                Manifest.permission.READ_EXTERNAL_STORAGE
                                            }
                                        )
                                    )
                                }
                            ) {
                                Text("Tomar Foto")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    } else {
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    }
                                    multiplePermissionsLauncher.launch(arrayOf(permission))
                                }
                            ) {
                                Text("Elegir de la Galería")
                            }
                        }
                    }
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