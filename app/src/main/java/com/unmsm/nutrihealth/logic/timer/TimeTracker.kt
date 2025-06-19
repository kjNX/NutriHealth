package com.unmsm.nutrihealth.logic.timer

interface TimeTracker {
    fun startResumeTimer(callback: (timeInMillis: Long) -> Unit)

    fun stopTimer()

    fun pauseTimer()

}