package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
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
//                    val userId = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.id = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.name = name
                    User.email = email
                    User.Target.mkRandom()
                    User.Plan.mkRandom()
                    User.StatTrak.mkRandom()

                    val userDoc = firestore.collection("users").document(User.id)
                    val userData = userDoc.collection("data")

                    val throwMe = { dbTask: Task<Void> ->
                        if(!dbTask.isSuccessful) throw RuntimeException()
                    }

                    val write = { document: DocumentReference, obj: Any ->
                        document.set(obj).addOnCompleteListener(throwMe)
                    }

                    try {
                        // Creating new entries
                        // I hate callback hell
                        write(userDoc, User)
                        write(userData.document("goal"), User.Target)
                        write(userData.document("plan"), User.Plan)
                        write(userData.document("stats"), User.StatTrak)

                        onResult(true, "")
                    } catch(_: RuntimeException) {
                        onResult(false, "Error al guardar datos del usuario.")
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

                    val userDoc = firestore.collection("users").document(User.id)
                    val userData = userDoc.collection("data")

                    val throwMe = { dbTask: Task<DocumentSnapshot> ->
                    }

                    val read = { ref: DocumentReference, exec: (DocumentSnapshot) -> Unit ->
                        ref.get().addOnCompleteListener { dbTask ->
                            val result = dbTask.result
                            if(!dbTask.isSuccessful || !result.exists())
                                throw RuntimeException()
                            exec(result)
                        }
                    }

                    try {
                        read(userDoc) { res ->
                            val data = res.data
                            User.name = data?.get("name").toString()
                            User.email = data?.get("email").toString()
                        }
                        read(userData.document("goal")) { res ->
                            val data = res.data
                            User.Target.startingWeight = data?.get("startingWeight").toString().toInt()
                            User.Target.currentWeight = data?.get("currentWeight").toString().toInt()
                            User.Target.targetWeight = data?.get("targetWeight").toString().toInt()
                            User.Target.updatePercentage()
                        }
                        read(userData.document("plan")) { res ->
                            val data = res.data
                            User.Plan.dailyCal = data?.get("dailyCal").toString().toInt()
                            User.Plan.protein = data?.get("protein").toString().toInt()
                            User.Plan.carbs = data?.get("carbs").toString().toInt()
                            User.Plan.fats = data?.get("fats").toString().toInt()
                        }
                        read(userData.document("stats")) { res ->
                            val data = res.data
                            User.StatTrak.time = data?.get("time").toString().toInt()
                            User.StatTrak.mileage = data?.get("mileage").toString().toFloat()
                            User.StatTrak.cal = data?.get("cal").toString().toInt()
                            User.StatTrak.avgSpeed = data?.get("avgSpeed").toString().toFloat()
                        }
                        onResult(true, "")
                    } catch (_: RuntimeException) {
                        onResult(false, "Verificación de integridad fallida. Contacte al administrador")
                    }
/*
                    firestore.collection("users").document(User.id).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                onResult(true, "")
                            } else {
                                auth.signOut()
                                onResult(false, "Este usuario no está registrado en NutriHealth.")
                            }
                        }
                        .addOnFailureListener {
                            onResult(false, "No se pudo verificar el usuario en la base de datos.")
                        }*/
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