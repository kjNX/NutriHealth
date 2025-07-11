package com.unmsm.nutrihealth.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val auth: FirebaseAuth) {

    suspend fun signIn(email: String, password: String): Result<AuthResult> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(email: String, password: String): Result<AuthResult> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signInWithCredential(credential: AuthCredential): Result<AuthResult> = runCatching {
        auth.signInWithCredential(credential).await()
    }

    fun signOut() = auth.signOut()

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun deleteAccount(): Result<Unit> = runCatching {
        auth.currentUser?.delete()?.await() ?: throw FirebaseAuthException(
            "NO_CURRENT_USER", 
            "No user is currently signed in"
        )
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) =
        auth.addAuthStateListener(listener)

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) =
        auth.removeAuthStateListener(listener)
}
