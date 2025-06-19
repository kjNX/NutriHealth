package com.unmsm.nutrihealth.data.model

import com.unmsm.nutrihealth.data.model.LocationTrackingInfo


interface LocationTrackingManager {
    fun setCallback(locationCallback: LocationCallback)

    fun removeCallback()

    interface LocationCallback {
        fun onLocationUpdate(results: List<com.unmsm.nutrihealth.data.model.LocationTrackingInfo>)
    }
}