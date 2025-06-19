package com.unmsm.nutrihealth.data.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.Date

object DBConverters {

    @TypeConverter
    fun fromBitmapToByteArray(bitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.toByteArray()
        }
    }

    @TypeConverter
    fun fromByteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromDateToMillis(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromMillisToDate(millis: Long): Date {
        return Date(millis)
    }
}
