package com.unmsm.nutrihealth.ui.util

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
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
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraOrGalleryPicker(
    context: Context,
    onImagePicked: (Uri) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Crear un archivo temporal para la foto
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir("Pictures")
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    // Obtener Uri para el archivo temporal
    fun getPhotoFileUri(): Uri {
        val photoFile = createImageFile()
        return FileProvider.getUriForFile(
            context,
            "com.unmsm.nutrihealth.fileprovider",
            photoFile
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            try {
                onImagePicked(photoUri!!)
            } catch (e: Exception) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error al procesar la imagen: ${e.message}")
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("No se pudo capturar la imagen")
            }
        }
        showDialog = false
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onImagePicked(it)
        }
        showDialog = false
    }

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.CAMERA, false) -> {
                try {
                    photoUri = getPhotoFileUri()
                    cameraLauncher.launch(photoUri)
                } catch (ex: Exception) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Error al iniciar la cámara: ${ex.message}")
                    }
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
                                    multiplePermissionsLauncher.launch(
                                        arrayOf(Manifest.permission.CAMERA)
                                    )
                                }
                            ) {
                                Text("Tomar Foto")
                            }

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