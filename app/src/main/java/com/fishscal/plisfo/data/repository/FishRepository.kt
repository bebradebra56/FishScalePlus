package com.fishscal.plisfo.data.repository

import com.fishscal.plisfo.data.local.FishCatchDao
import com.fishscal.plisfo.data.model.FishCatch
import kotlinx.coroutines.flow.Flow

class FishRepository(private val dao: FishCatchDao) {
    
    fun getAllCatches(): Flow<List<FishCatch>> = dao.getAllCatches()
    
    suspend fun getCatchById(id: Long): FishCatch? = dao.getCatchById(id)
    
    fun getCatchesByType(fishType: String): Flow<List<FishCatch>> = 
        dao.getCatchesByType(fishType)
    
    fun getCatchesByDateRange(startDate: Long, endDate: Long): Flow<List<FishCatch>> =
        dao.getCatchesByDateRange(startDate, endDate)
    
    suspend fun getCatchesInRange(startDate: Long, endDate: Long): List<FishCatch> =
        dao.getCatchesInRange(startDate, endDate)
    
    fun getAllFishTypes(): Flow<List<String>> = dao.getAllFishTypes()
    
    suspend fun insertCatch(catch: FishCatch): Long = dao.insertCatch(catch)
    
    suspend fun updateCatch(catch: FishCatch) = dao.updateCatch(catch)
    
    suspend fun deleteCatch(catch: FishCatch) = dao.deleteCatch(catch)
    
    suspend fun deleteAllCatches() = dao.deleteAllCatches()
    
    fun getTotalCatchesCount(): Flow<Int> = dao.getTotalCatchesCount()
    
    fun getAverageWeight(): Flow<Float?> = dao.getAverageWeight()
    
    fun getMaxWeight(): Flow<Float?> = dao.getMaxWeight()
    
    fun getMaxLength(): Flow<Float?> = dao.getMaxLength()
    
    fun getUniqueFishTypesCount(): Flow<Int> = dao.getUniqueFishTypesCount()
}

