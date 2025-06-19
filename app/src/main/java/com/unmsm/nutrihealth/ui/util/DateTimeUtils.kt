package com.unmsm.nutrihealth.ui.util

import java.util.concurrent.TimeUnit

object DateTimeUtils {

    fun getFormattedStopwatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)

        return if (!includeMillis) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            milliseconds /= 10
            String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds)
        }
    }
}
