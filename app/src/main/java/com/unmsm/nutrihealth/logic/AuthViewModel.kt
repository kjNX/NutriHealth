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
import com.unmsm.nutrihealth.data.model.UserData
import com.unmsm.nutrihealth.data.model.UserTarget
import com.unmsm.nutrihealth.data.repository.FirebaseAuthManager
import com.unmsm.nutrihealth.data.repository.FirebaseUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@HiltViewModel
class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
//    @Inject lateinit var firebaseAuthManager: FirebaseAuthManager
//    @Inject lateinit var firebaseUserRepository: FirebaseUserRepository

    fun signup(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        /*firebaseAuthManager.signUp(email, password) {
            if(it.isSuccessful) {
                firebaseUserRepository.getUser(firebaseAuthManager.currentUser!!.uid)

            }
        }*/
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
//                    val userId = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.id = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.name = name
                    User.email = email

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
                        write(userData.document("goal"), UserData)
                        write(userData.document("plan"), UserTarget)
//                        write(userData.document("stats"), UserStatTrak)

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
//        firebaseAuthManager.signIn(email, password) { task ->
//            if(task.isSuccessful) firebaseUserRepository.getUser(auth.currentUser!!.uid)
//        }
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
//                            UserData.weight = data?.get("weight").toString().toFloat()
//                            UserData.height = data?.get("height").toString().toInt()
//                            UserData.age = data?.get("age").toString().toInt()
//                            UserData.gender = UserData.Gender(data?.get("gender").toString().toInt())
                        }
                        read(userData.document("plan")) { res ->
                            val data = res.data
                            UserTarget.dailyCal = data?.get("dailyCal").toString().toInt()
                            UserTarget.protein = data?.get("protein").toString().toInt()
                            UserTarget.carbs = data?.get("carbs").toString().toInt()
                            UserTarget.fats = data?.get("fats").toString().toInt()
//                            UserTarget.priority = data?.get("priority").toString()
                        }
//                        read(userData.document("stats")) { res ->
//                            val data = res.data
//                            User.StatTrak.time = data?.get("time").toString().toInt()
//                            User.StatTrak.mileage = data?.get("mileage").toString().toFloat()
//                            User.StatTrak.cal = data?.get("cal").toString().toInt()
//                            User.StatTrak.avgSpeed = data?.get("avgSpeed").toString().toFloat()
//                        }
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