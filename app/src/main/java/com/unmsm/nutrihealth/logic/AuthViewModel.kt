package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.unmsm.nutrihealth.data.model.User

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun signup(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
//                    val userId = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.id = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.name = name
                    User.email = email

                    firestore.collection("users").document(User.id)
                        .set(User)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                onResult(true, "")
                            } else {
                                onResult(false, "Error al guardar datos del usuario.")
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
//                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    User.id = auth.currentUser?.uid ?: return@addOnCompleteListener

                    firestore.collection("users").document(User.id).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                User.name = document.get("name").toString()
                                User.email = document.get("email").toString()
                                onResult(true, "")
                            } else {
                                auth.signOut()
                                onResult(false, "Este usuario no está registrado en NutriHealth.")
                            }
                        }
                        .addOnFailureListener {
                            onResult(false, "No se pudo verificar el usuario en la base de datos.")
                        }
                } else {
                    val errorMessage = getFriendlyError(task.exception)
                    onResult(false, errorMessage)
                }
            }
    }

    // Traduce errores técnicos a mensajes más amigables
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