package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.User

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Método para crear un nuevo usuario con correo y contraseña
    fun signup(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Guardar la información del usuario en Firestore
                    User.id = it.result?.user?.uid ?: return@addOnCompleteListener
                    User.name = name
                    User.email = email
                    User.Target.mkRandom()
                    User.Plan.mkRandom()
                    User.StatTrak.mkRandom()

                    val userDoc = firestore.collection("users").document(User.id)
                    val userData = userDoc.collection("data")

                    try {
                        // Guardar los datos del usuario en Firestore
                        write(userDoc, User)
                        write(userData.document("goal"), User.Target)
                        write(userData.document("plan"), User.Plan)
                        write(userData.document("stats"), User.StatTrak)
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

    // Método para iniciar sesión con correo y contraseña
    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Recuperar los datos del usuario desde Firestore
                    User.id = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userDoc = firestore.collection("users").document(User.id)
                    val userData = userDoc.collection("data")

                    try {
                        // Leer los datos de Firestore
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
                    onResult(true, "Autenticación exitosa con Google.")
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
    // Método para iniciar sesión con Facebook
    fun loginWithFacebook(token: String, onResult: (Boolean, String) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onResult(true, "Autenticación exitosa con Facebook.")
                } else {
                    val errorMessage = getFriendlyError(task.exception)
                    onResult(false, errorMessage)
                }
            }
    }


    // Función para traducir los errores técnicos a mensajes más amigables
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
