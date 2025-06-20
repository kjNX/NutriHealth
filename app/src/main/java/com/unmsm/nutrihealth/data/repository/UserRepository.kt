package com.unmsm.nutrihealth.data.repository

import com.unmsm.nutrihealth.data.model.User

interface UserRepository {
    fun getUser(userId: String)
    fun getUserData(userId: String)
    fun getUserTarget(userId: String)
    fun createUser(userId: String)
    fun createUserData(userId: String)
    fun createUserTarget(userId: String)
}
