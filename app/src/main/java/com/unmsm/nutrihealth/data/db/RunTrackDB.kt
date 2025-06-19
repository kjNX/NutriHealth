package com.unmsm.nutrihealth.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.unmsm.nutrihealth.data.db.RunDao
import com.unmsm.nutrihealth.data.db.DBConverters
import com.unmsm.nutrihealth.data.model.Run

@Database(
    entities = [Run::class],
    version = 1,
)
@TypeConverters(DBConverters::class)
abstract class RunTrackDB : RoomDatabase() {

    companion object {
        const val RUN_TRACK_DB_NAME = "run_track_db"
    }

    abstract fun getRunDao(): RunDao
}
