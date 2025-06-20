package com.unmsm.nutrihealth.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageManager(private val storage: FirebaseStorage) {
    val storageRef = storage.reference

    fun uploadImage(fileUri: Uri, pathInStorage: String) =
        storageRef.child(pathInStorage).putFile(fileUri)

    fun downloadUrl(pathInStorage: String) =
        storageRef.child(pathInStorage).downloadUrl
}