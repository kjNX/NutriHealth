package com.unmsm.nutrihealth.data.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager(private val auth: FirebaseAuth) {
    fun signIn(email: String, password: String, onComplete: OnCompleteListener<AuthResult>) =
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onComplete)

    fun signUp(email: String, password: String, onComplete: OnCompleteListener<AuthResult>) =
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onComplete)

    fun signOut() = auth.signOut()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) =
        auth.addAuthStateListener(listener)

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) =
        auth.removeAuthStateListener(listener)
}