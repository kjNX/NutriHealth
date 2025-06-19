package com.unmsm.nutrihealth.ui.composable.pages.map.component

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.unmsm.nutrihealth.data.model.LocationInfo
import com.unmsm.nutrihealth.data.model.PathPoint
import com.unmsm.nutrihealth.data.model.firstLocationPoint
import com.unmsm.nutrihealth.data.model.lasLocationPoint
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import com.unmsm.nutrihealth.ui.util.GoogleMapUtils
import com.unmsm.nutrihealth.util.toLatLng
import com.unmsm.nutrihealth.R


@Composable
fun CurrentRunMap(
    modifier: Modifier = Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (Bitmap) -> Unit = {},
) {
    var mapSize by remember { mutableStateOf(Size.Zero) }
    var mapCenter by remember { mutableStateOf(Offset.Zero) }
    var isMapLoaded by remember { mutableStateOf(false) }

    // Usa el tema para la pantalla
    NutriHealthTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    val rect = it.boundsInRoot()
                    mapSize = rect.size
                    mapCenter = rect.center
                }
        ) {
            ShowMapLoadingProgressBar(!isMapLoaded)

            MapContent(
                pathPoints = pathPoints,
                isRunningFinished = isRunningFinished,
                mapCenter = mapCenter,
                mapSize = mapSize,
                onMapLoaded = { isMapLoaded = true },
                onSnapshot = onSnapshot
            )
        }
    }
}

@Composable
private fun MapContent(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    mapCenter: Offset,
    mapSize: Size,
    onMapLoaded: () -> Unit,
    onSnapshot: (Bitmap) -> Unit,
) {
    val uiSettings = remember {
        MapUiSettings(
            mapToolbarEnabled = false,
            compassEnabled = true,
            zoomControlsEnabled = false
        )
    }

    val cameraPositionState = rememberCameraPositionState()
    val lastLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.lasLocationPoint() }
    }

    LaunchedEffect(lastLocationPoint) {
        lastLocationPoint?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it.locationInfo.toLatLng(), 15f)
                )
            )
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded,
    ) {
        DrawPath(pathPoints, isRunningFinished)

        TakeScreenShot(
            take = isRunningFinished,
            mapCenter = mapCenter,
            mapSize = mapSize,
            pathPoints = pathPoints,
            onSnapshot = onSnapshot
        )
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun TakeScreenShot(
    take: Boolean,
    mapCenter: Offset,
    mapSize: Size,
    pathPoints: List<PathPoint>,
    onSnapshot: (Bitmap) -> Unit
) {
    MapEffect(key1 = take) { map ->
        if (take) {
            GoogleMapUtils.takeSnapshot(
                map, pathPoints, mapCenter, onSnapshot,
                snapshotSideLength = mapSize.width / 2f
            )
        }
    }
}

@Composable
private fun DrawPath(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean
) {
    val context = LocalContext.current
    val lastMarker = rememberMarkerState()
    val firstMarker = rememberMarkerState()
    val lastPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.lasLocationPoint() }
    }
    val firstPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.firstLocationPoint() }
    }

    val density = LocalDensity.current
    val colorPrimary = Color(0xFF4CAF50)  // Asegúrate de definir un color válido
    val flagColor = Color.Red.toArgb()    // Color del marcador final
    val startColor = Color(0xFF388E3C).toArgb()  // Color del marcador inicial

    LaunchedEffect(lastPoint) {
        lastPoint?.let {
            val latLng = it.locationInfo.toLatLng()
            lastMarker.position = latLng
        }
    }

    // Polylines
    val locationInfoList = mutableListOf<LocationInfo>()
    pathPoints.forEach { point ->
        when (point) {
            is PathPoint.LocationPoint -> locationInfoList.add(point.locationInfo)
            is PathPoint.EmptyLocationPoint -> {
                if (locationInfoList.isNotEmpty()) {
                    Polyline(points = locationInfoList.map { it.toLatLng() }, color = colorPrimary)
                    locationInfoList.clear()
                }
            }
        }
    }
    if (locationInfoList.isNotEmpty()) {
        Polyline(points = locationInfoList.map { it.toLatLng() }, color = colorPrimary)
    }

    // Final marker (Red flag or Dot)
    val endIcon = GoogleMapUtils.bitmapDescriptorFromVector(
        context,
        R.drawable.ic_location_marker,
        flagColor,
        50 // Tamaño del ícono
    )
    Marker(icon = endIcon, state = lastMarker, visible = isRunningFinished)

    // Start marker (Green flag)
    firstPoint?.let {
        val startIcon = GoogleMapUtils.bitmapDescriptorFromVector(
            context,
            R.drawable.ic_location_marker,
            startColor,
            50 // Tamaño del ícono
        )
        Marker(icon = startIcon, state = rememberMarkerState(position = it.locationInfo.toLatLng()))
    }
}

@Composable
private fun ShowMapLoadingProgressBar(visible: Boolean) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = visible,
        enter = EnterTransition.None,
        exit = fadeOut(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize()
        )
    }
}
