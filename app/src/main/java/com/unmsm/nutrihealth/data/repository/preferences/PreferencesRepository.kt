package com.unmsm.nutrihealth_app.data.preferences

interface PreferencesRepository {
    suspend fun<T> getValue(key: String, defaultValue: T) : T
    suspend fun<T> setValue(key: String, value: T)
}