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
import com.unmsm.nutrihealth.data.model.UserData
import com.unmsm.nutrihealth.data.model.UserTarget

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

                    val userDoc = firestore.collection("users").document(User.id)
                    val userData = userDoc.collection("data")

                    try {
                        write(userDoc, User)
                        write(userData.document("goal"), UserData)
                        write(userData.document("plan"), UserTarget)

                        onResult(true, "")
                    } catch (e: Exception) {
                        onResult(false, "Error al guardar los datos del usuario.")
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

                    val userDoc = firestore.collection("users").document(User.id)
                    val userData = userDoc.collection("data")

                    try {
                        read(userDoc) { res ->
                            val data = res.data
                            User.name = data?.get("name").toString()
                            User.email = data?.get("email").toString()
                        }
                        read(userData.document("goal")) { res ->
                            val data = res.data
                        }
                        read(userData.document("plan")) { res ->
                            val data = res.data
                            UserTarget.dailyCal = data?.get("dailyCal").toString().toInt()
                            UserTarget.protein = data?.get("protein").toString().toInt()
                            UserTarget.carbs = data?.get("carbs").toString().toInt()
                            UserTarget.fats = data?.get("fats").toString().toInt()
                        }

                        onResult(true, "")
                    } catch (e: Exception) {
                        onResult(false, "Error al obtener los datos del usuario.")
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
                        // Asignamos los valores a User para tenerlos disponibles
                        User.id = it.uid
                        User.name = it.displayName ?: "Nombre desconocido"
                        User.email = it.email ?: "Correo desconocido"

                        val userDoc = firestore.collection("users").document(it.uid)
                        userDoc.get().addOnCompleteListener { readTask ->
                            if (readTask.isSuccessful && readTask.result != null && readTask.result.exists()) {
                                // Si el usuario ya existe, recuperamos sus datos
                                val userData = readTask.result?.data
                                User.name = userData?.get("name").toString()
                                User.email = userData?.get("email").toString()
                                onResult(true, "Datos recuperados exitosamente.")
                            } else {
                                // Si el usuario no existe, lo creamos en Firestore
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



    // Función para escribir datos en Firestore
    private fun write(document: DocumentReference, obj: Any) {
        document.set(obj).addOnCompleteListener {
            if (!it.isSuccessful) throw RuntimeException()
        }
    }

    // Función para leer datos desde Firestore
    private fun read(ref: DocumentReference, exec: (DocumentSnapshot) -> Unit) {
        ref.get().addOnCompleteListener { task ->
            val result = task.result
            if (!task.isSuccessful || result == null || !result.exists()) throw RuntimeException()
            exec(result)
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