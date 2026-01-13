package com.fishscal.plisfo.data.local

import androidx.room.*
import com.fishscal.plisfo.data.model.FishCatch
import kotlinx.coroutines.flow.Flow

@Dao
interface FishCatchDao {
    
    @Query("SELECT * FROM fish_catches ORDER BY date DESC")
    fun getAllCatches(): Flow<List<FishCatch>>
    
    @Query("SELECT * FROM fish_catches WHERE id = :id")
    suspend fun getCatchById(id: Long): FishCatch?
    
    @Query("SELECT * FROM fish_catches WHERE fishType = :fishType ORDER BY date DESC")
    fun getCatchesByType(fishType: String): Flow<List<FishCatch>>
    
    @Query("SELECT * FROM fish_catches WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getCatchesByDateRange(startDate: Long, endDate: Long): Flow<List<FishCatch>>
    
    @Query("SELECT * FROM fish_catches WHERE date >= :startDate AND date <= :endDate")
    suspend fun getCatchesInRange(startDate: Long, endDate: Long): List<FishCatch>
    
    @Query("SELECT DISTINCT fishType FROM fish_catches")
    fun getAllFishTypes(): Flow<List<String>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatch(catch: FishCatch): Long
    
    @Update
    suspend fun updateCatch(catch: FishCatch)
    
    @Delete
    suspend fun deleteCatch(catch: FishCatch)
    
    @Query("DELETE FROM fish_catches")
    suspend fun deleteAllCatches()
    
    @Query("SELECT COUNT(*) FROM fish_catches")
    fun getTotalCatchesCount(): Flow<Int>
    
    @Query("SELECT AVG(weight) FROM fish_catches")
    fun getAverageWeight(): Flow<Float?>
    
    @Query("SELECT MAX(weight) FROM fish_catches")
    fun getMaxWeight(): Flow<Float?>
    
    @Query("SELECT MAX(length) FROM fish_catches WHERE length IS NOT NULL")
    fun getMaxLength(): Flow<Float?>
    
    @Query("SELECT COUNT(DISTINCT fishType) FROM fish_catches")
    fun getUniqueFishTypesCount(): Flow<Int>
}

