package com.fishscal.plisfo.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        private val LENGTH_UNIT = stringPreferencesKey("length_unit")
    }
    
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }
    
    val weightUnit: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WEIGHT_UNIT] ?: "kg"
    }
    
    suspend fun setWeightUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[WEIGHT_UNIT] = unit
        }
    }
    
    val lengthUnit: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LENGTH_UNIT] ?: "cm"
    }
    
    suspend fun setLengthUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[LENGTH_UNIT] = unit
        }
    }
    
    suspend fun resetData() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = false
            preferences[WEIGHT_UNIT] = "kg"
            preferences[LENGTH_UNIT] = "cm"
        }
    }
}

