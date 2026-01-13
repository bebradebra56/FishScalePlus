package com.fishscal.plisfo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishscal.plisfo.data.datastore.PreferencesManager
import com.fishscal.plisfo.data.model.FishCatch
import com.fishscal.plisfo.data.repository.FishRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class FishViewModel(
    private val repository: FishRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    // All catches
    val allCatches: StateFlow<List<FishCatch>> = repository.getAllCatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Statistics
    val totalCatches: StateFlow<Int> = repository.getTotalCatchesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val averageWeight: StateFlow<Float?> = repository.getAverageWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val maxWeight: StateFlow<Float?> = repository.getMaxWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val maxLength: StateFlow<Float?> = repository.getMaxLength()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val uniqueFishTypes: StateFlow<Int> = repository.getUniqueFishTypesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    // Preferences
    val isOnboardingCompleted: StateFlow<Boolean> = preferencesManager.isOnboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val weightUnit: StateFlow<String> = preferencesManager.weightUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "kg")
    
    val lengthUnit: StateFlow<String> = preferencesManager.lengthUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "cm")
    
    // Filtered catches by fish type
    private val _selectedFishType = MutableStateFlow<String?>(null)
    val selectedFishType: StateFlow<String?> = _selectedFishType
    
    val filteredCatches: StateFlow<List<FishCatch>> = _selectedFishType
        .flatMapLatest { fishType ->
            if (fishType != null) {
                repository.getCatchesByType(fishType)
            } else {
                repository.getAllCatches()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Get catches grouped by fish type
    val catchesByType: StateFlow<Map<String, List<FishCatch>>> = allCatches
        .map { catches -> catches.groupBy { it.fishType } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    
    // Best catches
    val heaviestCatch: StateFlow<FishCatch?> = allCatches
        .map { catches -> catches.maxByOrNull { it.weight } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val longestCatch: StateFlow<FishCatch?> = allCatches
        .map { catches -> catches.filter { it.length != null }.maxByOrNull { it.length!! } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val bestDay: StateFlow<Pair<Long, List<FishCatch>>?> = allCatches
        .map { catches ->
            catches.groupBy { 
                Calendar.getInstance().apply { 
                    timeInMillis = it.date 
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }.maxByOrNull { it.value.sumOf { catch -> catch.weight.toDouble() } }
                ?.toPair()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    // Functions
    fun setFilterByFishType(fishType: String?) {
        _selectedFishType.value = fishType
    }
    
    suspend fun getCatchById(id: Long): FishCatch? {
        return repository.getCatchById(id)
    }
    
    fun insertCatch(catch: FishCatch) {
        viewModelScope.launch {
            repository.insertCatch(catch)
        }
    }
    
    fun updateCatch(catch: FishCatch) {
        viewModelScope.launch {
            repository.updateCatch(catch)
        }
    }
    
    fun deleteCatch(catch: FishCatch) {
        viewModelScope.launch {
            repository.deleteCatch(catch)
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted(true)
        }
    }
    
    fun setWeightUnit(unit: String) {
        viewModelScope.launch {
            preferencesManager.setWeightUnit(unit)
        }
    }
    
    fun setLengthUnit(unit: String) {
        viewModelScope.launch {
            preferencesManager.setLengthUnit(unit)
        }
    }
    
    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllCatches()
            preferencesManager.resetData()
        }
    }
    
    suspend fun getCatchesForExport(startDate: Long, endDate: Long): List<FishCatch> {
        return repository.getCatchesInRange(startDate, endDate)
    }
}

