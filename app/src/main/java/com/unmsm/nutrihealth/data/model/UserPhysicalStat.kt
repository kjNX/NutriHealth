package com.unmsm.nutrihealth.data.model

object UserPhysicalStat {
    var time: Int = 0
    var mileage: Float = 0f
    var cal: Int = 0
    var avgSpeed: Float = 0f

    fun reset() {
        time = 0
        mileage = 0f
        cal = 0
        avgSpeed = 0f
    }

    fun twoBitNumber(i: Int): String = when(i) {
        0 -> "00"
        in 1..9 -> "0$i"
        else -> "$i"
    }

    fun parseTime(): String {
        var aux = time
        val hours = (aux / 3600).toInt()
        aux %= 3600
        val minutes = (aux / 60).toInt()
        aux %= 60

        return "${twoBitNumber(hours)}:${twoBitNumber(minutes)}:${twoBitNumber(aux)}"
    }
}