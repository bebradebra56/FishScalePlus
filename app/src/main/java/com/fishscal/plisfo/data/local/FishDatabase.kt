package com.fishscal.plisfo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fishscal.plisfo.data.model.FishCatch

@Database(
    entities = [FishCatch::class],
    version = 1,
    exportSchema = false
)
abstract class FishDatabase : RoomDatabase() {
    abstract fun fishCatchDao(): FishCatchDao
}

