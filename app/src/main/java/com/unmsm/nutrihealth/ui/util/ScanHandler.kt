package com.unmsm.nutrihealth.ui.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun CameraOrGalleryPicker(
    context: Context,
    onImagePicked: (Uri) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        // Guarda temporalmente y devuelve Uri
        bitmap?.let {
            val uri = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                it,
                "scanned_image",
                null
            )
            uri?.let { uriStr -> onImagePicked(Uri.parse(uriStr)) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onImagePicked(it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            showDialog = true
        }
    }

    // Mostrar opciones después del permiso
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            title = { Text("Selecciona fuente de imagen") },
            text = {
                Column {
                    Button(onClick = {
                        showDialog = false
                        cameraLauncher.launch(null)
                    }) { Text("Tomar Foto") }

                    Button(onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }) { Text("Elegir de la Galería") }
                }
            }
        )
    }

    // Lanza permiso al entrar
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
}
