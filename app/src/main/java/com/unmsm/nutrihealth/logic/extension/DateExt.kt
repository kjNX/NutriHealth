package com.unmsm.nutrihealth.logic.extension

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.toCalendar(): Calendar = Calendar.getInstance().also { it.time = this }

fun Date.getDisplayDate(): String =
    SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        .format(this)