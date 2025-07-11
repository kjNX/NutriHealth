package com.unmsm.nutrihealth_app.model.live

import java.util.Date

data class Message(
//    val userID: Int,
//    val actorID: Int,
    val date: Date,
    val content: String,
    val property: Boolean
)
