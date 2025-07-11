package com.unmsm.nutrihealth_app.model.live

import com.google.firebase.firestore.Exclude

// User define al usuario y sus datos base
data class User(
    val name: String,
    val email: String,
    val stage: Int = 0
) {
    constructor() : this("", "")
}
