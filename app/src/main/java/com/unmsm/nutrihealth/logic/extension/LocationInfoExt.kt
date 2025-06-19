package com.unmsm.nutrihealth.logic.extension

import com.google.android.gms.maps.model.LatLng

import com.unmsm.nutrihealth.data.model.LocationInfo

fun LocationInfo.toLatLng() = LatLng(
    latitude,
    longitude
)