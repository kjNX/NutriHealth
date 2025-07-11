package com.unmsm.nutrihealth.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Manager class for Firebase Authentication operations.
 * Provides methods for user authentication, registration, password recovery, and account management.
 */
class FirebaseAuthManager(private val auth: FirebaseAuth) {

    /**
     * Signs in a user with email and password.
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing AuthResult on success or Exception on failure
     */
    suspend fun signIn(email: String, password: String): Result<AuthResult> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * Creates a new user account with email and password.
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing AuthResult on success or Exception on failure
     */
    suspend fun signUp(email: String, password: String): Result<AuthResult> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    /**
     * Signs in a user with a credential (Google, Facebook, etc.).
     * 
     * @param credential The auth credential to sign in with
     * @return Result containing AuthResult on success or Exception on failure
     */
    suspend fun signInWithCredential(credential: AuthCredential): Result<AuthResult> = runCatching {
        auth.signInWithCredential(credential).await()
    }

    /**
     * Signs out the current user.
     */
    fun signOut() = auth.signOut()

    /**
     * Sends a password reset email to the specified email address.
     * 
     * @param email Email address to send the password reset link to
     * @return Result containing Unit on success or Exception on failure
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    /**
     * Deletes the current user account.
     * 
     * @return Result containing Unit on success or Exception on failure
     */
    suspend fun deleteAccount(): Result<Unit> = runCatching {
        auth.currentUser?.delete()?.await() ?: throw FirebaseAuthException(
            "NO_CURRENT_USER", 
            "No user is currently signed in"
        )
    }

    /**
     * Gets the current signed-in user.
     * 
     * @return The current FirebaseUser or null if no user is signed in
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Adds an auth state listener to receive authentication state changes.
     * 
     * @param listener The auth state listener to add
     */
    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) =
        auth.addAuthStateListener(listener)

    /**
     * Removes an auth state listener.
     * 
     * @param listener The auth state listener to remove
     */
    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) =
        auth.removeAuthStateListener(listener)
}
