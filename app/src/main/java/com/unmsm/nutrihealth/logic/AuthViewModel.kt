package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.unmsm.nutrihealth.data.model.User

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun signup(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    User.id = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.name = name
                    User.email = email

                    val userDoc = firestore.collection("user").document(User.id)
                    val userData = userDoc.collection("data")

                    val data = hashMapOf(
                        "name" to name,
                        "email" to email
                    )
                    userDoc.set(data).addOnCompleteListener { writeTask ->
                        if (writeTask.isSuccessful) {
                            onResult(true, "Registro exitoso")
                        } else {
                            onResult(false, "Error al guardar los datos del usuario.")
                        }
                    }

                } else {
                    val errorMessage = getFriendlyError(it.exception)
                    onResult(false, errorMessage)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    User.id = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val userDoc = firestore.collection("user").document(User.id)
                    val userData = userDoc.collection("data")

                    read(userDoc) { res ->
                        if (res != null) {
                            val data = res.data
                            User.name = data?.get("name")?.toString() ?: ""
                            User.email = data?.get("email")?.toString() ?: ""

                            read(userData.document("goal")) { goalRes ->
                                // Puedes mapear más campos si los necesitas aquí
                                onResult(true, "")
                            }

                        } else {
                            onResult(false, "No se encontraron los datos del usuario.")
                        }
                    }

                } else {
                    val errorMessage = getFriendlyError(task.exception)
                    onResult(false, errorMessage)
                }
            }
    }

    fun loginWithGoogle(idToken: String, onResult: (Boolean, String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        User.id = it.uid
                        User.name = it.displayName ?: "Nombre desconocido"
                        User.email = it.email ?: "Correo desconocido"

                        val userDoc = firestore.collection("users").document(it.uid)
                        userDoc.get().addOnCompleteListener { readTask ->
                            if (readTask.isSuccessful && readTask.result != null && readTask.result.exists()) {
                                val userData = readTask.result?.data
                                User.name = userData?.get("name")?.toString() ?: ""
                                User.email = userData?.get("email")?.toString() ?: ""
                                onResult(true, "Datos recuperados exitosamente.")
                            } else {
                                val userData = hashMapOf(
                                    "email" to it.email,
                                    "name" to it.displayName,
                                    "photoUrl" to it.photoUrl.toString()
                                )
                                userDoc.set(userData)
                                    .addOnCompleteListener { writeTask ->
                                        if (writeTask.isSuccessful) {
                                            onResult(true, "Autenticación exitosa con Google y datos guardados.")
                                        } else {
                                            onResult(false, "Error al guardar los datos del usuario.")
                                        }
                                    }
                            }
                        }
                    }
                } else {
                    val errorMessage = getFriendlyError(task.exception)
                    onResult(false, errorMessage)
                }
            }
    }

    // 🔐 Función para leer datos sin lanzar excepción
    private fun read(ref: DocumentReference, exec: (DocumentSnapshot?) -> Unit) {
        ref.get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null && task.result.exists()) {
                exec(task.result)
            } else {
                exec(null)
            }
        }
    }

    // 🧠 Traduce errores FirebaseAuth a mensajes entendibles
    private fun getFriendlyError(exception: Exception?): String {
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo electrónico no es válido."
            "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este correo."
            "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
            "ERROR_USER_DISABLED" -> "Esta cuenta ha sido desactivada."
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos fallidos. Inténtalo más tarde."
            else -> exception?.localizedMessage ?: "Error al iniciar sesión."
        }
    }
}
