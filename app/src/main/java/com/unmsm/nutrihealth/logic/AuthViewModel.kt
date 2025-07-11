package com.unmsm.nutrihealth.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.repository.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthViewModel"

/**
 * ViewModel for handling authentication operations.
 * Uses Firebase Authentication for login, signup, password recovery, and account deletion.
 */
class AuthViewModel(
    private val authManager: FirebaseAuthManager = FirebaseAuthManager(Firebase.auth),
    private val firestore: com.google.firebase.firestore.FirebaseFirestore = Firebase.firestore
) : ViewModel() {

    // UI state
    var errorMessage by mutableStateOf("")
        private set

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Creates a new user account with email and password.
     * Also stores user data in Firestore.
     *
     * @param name User's name
     * @param email User's email address
     * @param password User's password
     */
    suspend fun signup(name: String, email: String, password: String): Result<Unit> {
        _authState.value = AuthState.Loading

        return try {
            val authResult = authManager.signUp(email, password).getOrThrow()
            val uid = authResult.user?.uid ?: throw IllegalStateException("User ID is null after signup")

            // Update local user object
            User.id = uid
            User.name = name
            User.email = email

            // Store user data in Firestore
            val userDoc = firestore.collection("user").document(uid)
            val data = hashMapOf(
                "authID" to uid,
                "email" to email,
                "name" to name,
                "stage" to 0
            )

            userDoc.set(data).await()
            _authState.value = AuthState.Success("Registro exitoso")
            Result.success(Unit)
        } catch (e: Exception) {
            errorMessage = getFriendlyError(e)
            _authState.value = AuthState.Error(errorMessage)
            Result.failure(e)
        }
    }

    /**
     * Signs in a user with email and password.
     * Retrieves user data from Firestore after successful login.
     *
     * @param email User's email address
     * @param password User's password
     */
    suspend fun login(email: String, password: String): Result<Unit> {
        _authState.value = AuthState.Loading

        return try {
            val authResult = authManager.signIn(email, password).getOrThrow()
            val uid = authResult.user?.uid ?: throw IllegalStateException("User ID is null after login")

            // Update local user object
            User.id = uid

            // Retrieve user data from Firestore
            val userDoc = firestore.collection("user").document(uid)
            val docSnapshot = userDoc.get().await()

            if (docSnapshot.exists()) {
                val data = docSnapshot.data
                User.name = data?.get("name")?.toString() ?: ""
                User.email = data?.get("email")?.toString() ?: ""
                User.stage = (data?.get("stage") as? Long)?.toInt() ?: 0

                _authState.value = AuthState.Success("Inicio de sesión exitoso")
                Result.success(Unit)
            } else {
                errorMessage = "No se encontraron los datos del usuario."
                _authState.value = AuthState.Error(errorMessage)
                Result.failure(IllegalStateException(errorMessage))
            }
        } catch (e: Exception) {
            errorMessage = getFriendlyError(e)
            _authState.value = AuthState.Error(errorMessage)
            Result.failure(e)
        }
    }

    /**
     * Signs in a user with Google credentials.
     * Creates a new user record in Firestore if the user doesn't exist.
     *
     * @param idToken Google ID token
     */
    suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        _authState.value = AuthState.Loading

        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = authManager.signInWithCredential(credential).getOrThrow()
            val user = authResult.user ?: throw IllegalStateException("User is null after Google login")

            // Update local user object
            User.id = user.uid
            User.name = user.displayName ?: "Nombre desconocido"
            User.email = user.email ?: "Correo desconocido"

            // Check if user exists in Firestore
            val userDoc = firestore.collection("user").document(user.uid)
            val docSnapshot = userDoc.get().await()

            if (docSnapshot.exists()) {
                // User exists, update local data
                val data = docSnapshot.data
                User.name = data?.get("name")?.toString() ?: User.name
                User.email = data?.get("email")?.toString() ?: User.email
                User.stage = (data?.get("stage") as? Long)?.toInt() ?: 0

                _authState.value = AuthState.Success("Datos recuperados exitosamente")
            } else {
                // New user, create record in Firestore
                val userData = hashMapOf(
                    "authID" to user.uid,
                    "email" to user.email,
                    "name" to user.displayName,
                    "stage" to 0
                )

                userDoc.set(userData).await()
                _authState.value = AuthState.Success("Autenticación exitosa con Google y datos guardados")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            errorMessage = getFriendlyError(e)
            _authState.value = AuthState.Error(errorMessage)
            Result.failure(e)
        }
    }

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email Email address to send the password reset link to
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        _authState.value = AuthState.Loading

        return try {
            authManager.sendPasswordResetEmail(email).getOrThrow()
            _authState.value = AuthState.Success("Se ha enviado un correo para restablecer tu contraseña")
            Result.success(Unit)
        } catch (e: Exception) {
            errorMessage = getFriendlyError(e)
            _authState.value = AuthState.Error(errorMessage)
            Result.failure(e)
        }
    }

    /**
     * Deletes the current user account.
     * Also removes user data from Firestore.
     */
    suspend fun deleteAccount(): Result<Unit> {
        _authState.value = AuthState.Loading

        return try {
            // Delete user data from Firestore first
            if (User.id.isNotEmpty()) {
                val userDoc = firestore.collection("user").document(User.id)
                userDoc.delete().await()
            }

            // Then delete the authentication account
            authManager.deleteAccount().getOrThrow()

            // Reset local user data
            User.reset()

            _authState.value = AuthState.Success("Cuenta eliminada exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            errorMessage = getFriendlyError(e)
            _authState.value = AuthState.Error(errorMessage)
            Result.failure(e)
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        authManager.signOut()
        User.reset()
        _authState.value = AuthState.Idle
    }

    /**
     * Reads data from a Firestore document reference without throwing exceptions.
     *
     * @param ref Document reference to read from
     * @param exec Callback to execute with the document snapshot
     */
    private suspend fun read(ref: DocumentReference): Result<DocumentSnapshot> = runCatching {
        ref.get().await()
    }

    /**
     * Translates Firebase Auth errors to user-friendly messages.
     *
     * @param exception The exception to translate
     * @return A user-friendly error message
     */
    private fun getFriendlyError(exception: Exception?): String {
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo electrónico no es válido."
            "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este correo."
            "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
            "ERROR_USER_DISABLED" -> "Esta cuenta ha sido desactivada."
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos fallidos. Inténtalo más tarde."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado."
            "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil."
            "ERROR_OPERATION_NOT_ALLOWED" -> "Operación no permitida."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "Ya existe una cuenta con este correo pero con diferentes credenciales."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "Esta operación es sensible y requiere autenticación reciente. Inicia sesión nuevamente."
            else -> exception?.localizedMessage ?: "Error desconocido."
        }
    }
}

/**
 * Represents the current state of authentication operations.
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
