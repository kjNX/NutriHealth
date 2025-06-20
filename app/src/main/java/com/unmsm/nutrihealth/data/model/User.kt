package com.unmsm.nutrihealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
object User {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var email: String = ""

    fun reset() {
        id = ""
        name = ""
        email = ""
    }
}
