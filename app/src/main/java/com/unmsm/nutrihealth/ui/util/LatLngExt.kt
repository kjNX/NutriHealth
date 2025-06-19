package com.unmsm.nutrihealth.util

import com.google.android.gms.maps.model.LatLng
import com.unmsm.nutrihealth.data.model.LocationInfo

fun LocationInfo.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}
