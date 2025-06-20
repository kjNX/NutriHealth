package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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

                    try {
                        // Creating new entries
                        // I hate callback hell
                        write(userDoc, User)
                        write(userData.document("goal"), UserData)
                        write(userData.document("plan"), UserTarget)
//                        write(userData.document("stats"), UserStatTrak)

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

                    try {
                        read(userDoc) { res ->
                            val data = res.data
                            User.name = data?.get("name").toString()
                            User.email = data?.get("email").toString()
                        }
                        /*
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
                        */
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

    // Método para iniciar sesión con Google
    fun loginWithGoogle(idToken: String, onResult: (Boolean, String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Guardar el usuario en Firestore
                    user?.let {
                        val userDoc = firestore.collection("users").document(it.uid)
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
                } else {
                    val errorMessage = getFriendlyError(task.exception)
                    onResult(false, errorMessage)
                }
            }
    }



    // Método para iniciar sesión con Facebook
    fun loginWithFacebook(token: String, onResult: (Boolean, String) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Guardar el usuario en Firestore
                    user?.let {
                        val userDoc = firestore.collection("users").document(it.uid)
                        val userData = hashMapOf(
                            "email" to it.email,
                            "name" to it.displayName,
                            "photoUrl" to it.photoUrl.toString()
                        )
                        userDoc.set(userData)
                            .addOnCompleteListener { writeTask ->
                                if (writeTask.isSuccessful) {
                                    onResult(true, "Autenticación exitosa con Facebook y datos guardados.")
                                } else {
                                    onResult(false, "Error al guardar los datos del usuario.")
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