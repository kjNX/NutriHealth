package com.unmsm.nutrihealth.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.unmsm.nutrihealth.data.model.PathPoint

object GoogleMapUtils {

    // Función para crear un BitmapDescriptor a partir de un vector Drawable
    fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorResId: Int,
        tint: Int,
        sizeInPx: Int
    ): BitmapDescriptor? {
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        val bitmap = Bitmap.createBitmap(sizeInPx, sizeInPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        vectorDrawable.setBounds(0, 0, sizeInPx, sizeInPx)
        vectorDrawable.setTint(tint)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Función para tomar una captura de pantalla del mapa
    fun takeSnapshot(
        map: GoogleMap,
        pathPoints: List<PathPoint>,
        mapCenter: androidx.compose.ui.geometry.Offset,
        onSnapshot: (Bitmap) -> Unit,
        snapshotSideLength: Float
    ) {
        map.snapshot { bitmap ->
            try {
                bitmap?.let { // Safe call para verificar si 'bitmap' no es null
                    val projection: Projection = map.projection
                    val centerLatLng = projection.fromScreenLocation(
                        Point(mapCenter.x.toInt(), mapCenter.y.toInt())
                    )
                    val centerScreenPoint = projection.toScreenLocation(centerLatLng)
                    val left = (centerScreenPoint.x - snapshotSideLength / 2f).toInt().coerceAtLeast(0)
                    val top = (centerScreenPoint.y - snapshotSideLength / 2f).toInt().coerceAtLeast(0)
                    val right = (centerScreenPoint.x + snapshotSideLength / 2f).toInt()
                        .coerceAtMost(bitmap.width)
                    val bottom = (centerScreenPoint.y + snapshotSideLength / 2f).toInt()
                        .coerceAtMost(bitmap.height)

                    // Crear un bitmap recortado a partir de la captura de pantalla
                    val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
                    onSnapshot(croppedBitmap)
                } ?: run {
                    // Si 'bitmap' es null, proporciona un fallback (bitmap vacío)
                    Log.e("GoogleMapUtils", "Bitmap is null")
                    onSnapshot(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) // Bitmap vacío de respaldo
                }
            } catch (e: Exception) {
                Log.e("GoogleMapUtils", "Error while taking snapshot: ${e.localizedMessage}")
                // Si algo falla, asegúrate de pasar un bitmap vacío
                onSnapshot(bitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
            }
        }
    }
}
