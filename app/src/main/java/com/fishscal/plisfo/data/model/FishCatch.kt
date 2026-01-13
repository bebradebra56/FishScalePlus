package com.fishscal.plisfo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fish_catches")
data class FishCatch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fishType: String,
    val weight: Float, // in kg
    val length: Float? = null, // in cm
    val date: Long, // timestamp
    val notes: String? = null,
    val location: String? = null,
    val weather: String? = null,
    val bait: String? = null
)

