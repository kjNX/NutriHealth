package com.unmsm.nutrihealth

import android.app.Application
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NutriHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        
        // Verificar disponibilidad de Google Play Services
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(this)
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            Timber.e("Google Play Services no está disponible: $resultCode")
        }
    }
}
