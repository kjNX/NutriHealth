package com.unmsm.nutrihealth.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import com.unmsm.nutrihealth.data.model.User.email
import com.unmsm.nutrihealth.data.model.User.name

@Entity(tableName = "user_data")
object UserData {
    var id: Int = 0
    var weight: Float = 0f
    var height: Int = 0
    var age: Int = 0
    var gender: Gender = Gender.Male

    fun reset() {
        id = 0
        name = ""
        email = ""
    }
    enum class Gender(val int: Int, val icon: ImageVector, val bonus: Int) {
        Male(0, Icons.Default.Male, 5),
        Female(1, Icons.Default.Female, -161)
    }
}
