package com.fishscal.plisfo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fishscal.plisfo.data.datastore.PreferencesManager
import com.fishscal.plisfo.data.repository.FishRepository

class ViewModelFactory(
    private val repository: FishRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FishViewModel::class.java)) {
            return FishViewModel(repository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

