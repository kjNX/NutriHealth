package com.unmsm.nutrihealth_app.data.auth

interface AuthRepository {
    val currentSession : String // returns the uid of the current session or an empty string

    suspend fun login(email: String, password: String) : Boolean
    suspend fun register(email: String, password: String) : Boolean
    suspend fun updateEmail(email: String) : Boolean
    suspend fun updatePassword(password: String) : Boolean
    fun logout()
    suspend fun delete() : Boolean
}