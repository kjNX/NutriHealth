package com.unmsm.nutrihealth.data.repository.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepository : AuthRepository {
    private val instance = FirebaseAuth.getInstance()

    override val currentSession
        get() = instance.currentUser?.uid ?: ""

    override suspend fun login(email: String, password: String) : Boolean = try {
        instance.signInWithEmailAndPassword(email, password).await()
        true
    } catch (e: Exception) {
        Log.d(TAG, "login: ${e.message}")
        false
    }

    override suspend fun register(email: String, password: String) : Boolean = try {
        instance.createUserWithEmailAndPassword(email, password).await()
        true
    } catch (e: Exception) {
        Log.d(TAG, "register: ${e.message}")
        false
    }

    override suspend fun updateEmail(email: String): Boolean = try {
        instance.currentUser!!.verifyBeforeUpdateEmail(email).await()
        true
    } catch (e: Exception) {
        Log.d(TAG, "updateEmail: ${e.message}")
        false
    }

    override suspend fun updatePassword(password: String): Boolean = try {
        instance.currentUser!!.updatePassword(password).await()
        true
    } catch (e: Exception) {
        Log.d(TAG, "updatePassword: ${e.message}")
        false
    }

    override fun logout() { instance.signOut() }

    override suspend fun delete() : Boolean = try {
        instance.currentUser?.delete()?.await()
        true
    } catch (e: Exception) {
        Log.d(TAG, "delete: ${e.message}")
        false
    }
}
