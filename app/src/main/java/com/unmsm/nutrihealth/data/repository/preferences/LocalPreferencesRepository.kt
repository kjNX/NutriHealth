package com.unmsm.nutrihealth.data.repository.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nutrihealth_settings")

class LocalPreferencesRepository(private val context: Context) : PreferencesRepository {
    @Suppress("UNCHECKED_CAST")
    private fun <T> preferencesKey(key: String, defaultValue: T): Preferences.Key<T> {
        return when (defaultValue) {
            is Int -> intPreferencesKey(key) as Preferences.Key<T>
            is Long -> longPreferencesKey(key) as Preferences.Key<T>
            is Boolean -> booleanPreferencesKey(key) as Preferences.Key<T>
            is Float -> floatPreferencesKey(key) as Preferences.Key<T>
            is Double -> doublePreferencesKey(key) as Preferences.Key<T>
            is String -> stringPreferencesKey(key) as Preferences.Key<T>
            is Set<*> -> stringSetPreferencesKey(key) as Preferences.Key<T>
            else -> throw IllegalArgumentException("Unsupported type for DataStore: ${defaultValue!!::class.java.simpleName}")
        }
    }

    override suspend fun <T> getValue(key: String, defaultValue: T): T = context.dataStore.data
        .map { preferences -> preferences[preferencesKey(key, defaultValue)] ?: defaultValue }
        .first()

    override suspend fun <T> setValue(key: String, value: T) {
        context.dataStore
            .edit { preferences -> preferences[preferencesKey(key, value)] = value }
    }
}