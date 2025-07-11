package com.unmsm.nutrihealth.di

import android.content.Context
import com.unmsm.nutrihealth.data.repository.preferences.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        // Create a simple implementation that delegates to SharedPreferences
        return object : PreferencesRepository {
            override suspend fun <T> getValue(key: String, defaultValue: T): T {
                @Suppress("UNCHECKED_CAST")
                return when (defaultValue) {
                    is Boolean -> context.getSharedPreferences("nutrihealth_prefs", Context.MODE_PRIVATE)
                        .getBoolean(key, defaultValue) as T
                    is String -> context.getSharedPreferences("nutrihealth_prefs", Context.MODE_PRIVATE)
                        .getString(key, defaultValue) as T
                    is Int -> context.getSharedPreferences("nutrihealth_prefs", Context.MODE_PRIVATE)
                        .getInt(key, defaultValue) as T
                    is Long -> context.getSharedPreferences("nutrihealth_prefs", Context.MODE_PRIVATE)
                        .getLong(key, defaultValue) as T
                    is Float -> context.getSharedPreferences("nutrihealth_prefs", Context.MODE_PRIVATE)
                        .getFloat(key, defaultValue) as T
                    else -> defaultValue
                }
            }

            override suspend fun <T> setValue(key: String, value: T) {
                val editor = context.getSharedPreferences("nutrihealth_prefs", Context.MODE_PRIVATE).edit()
                when (value) {
                    is Boolean -> editor.putBoolean(key, value)
                    is String -> editor.putString(key, value)
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is Float -> editor.putFloat(key, value)
                }
                editor.apply()
            }
        }
    }
}
