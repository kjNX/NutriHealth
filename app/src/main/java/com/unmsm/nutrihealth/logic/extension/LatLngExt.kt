package com.sdevprem.runtrack.common.extension

import com.google.android.gms.maps.model.LatLng

import com.unmsm.nutrihealth.data.model.LocationInfo

fun LatLng.toLocationInfo() = LocationInfo(
    longitude = longitude,
    latitude = latitude
)