package com.fishscal.plisfo.trng.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishscal.plisfo.trng.data.shar.FishScalePlusSharedPreference
import com.fishscal.plisfo.trng.data.utils.FishScalePlusSystemService
import com.fishscal.plisfo.trng.domain.usecases.FishScalePlusGetAllUseCase
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusAppsFlyerState
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FishScalePlusLoadViewModel(
    private val fishScalePlusGetAllUseCase: FishScalePlusGetAllUseCase,
    private val fishScalePlusSharedPreference: FishScalePlusSharedPreference,
    private val fishScalePlusSystemService: FishScalePlusSystemService
) : ViewModel() {

    private val _fishScalePlusHomeScreenState: MutableStateFlow<FishScalePlusHomeScreenState> =
        MutableStateFlow(FishScalePlusHomeScreenState.FishScalePlusLoading)
    val fishScalePlusHomeScreenState = _fishScalePlusHomeScreenState.asStateFlow()

    private var fishScalePlusGetApps = false


    init {
        viewModelScope.launch {
            when (fishScalePlusSharedPreference.fishScalePlusAppState) {
                0 -> {
                    if (fishScalePlusSystemService.fishScalePlusIsOnline()) {
                        FishScalePlusApplication.fishScalePlusConversionFlow.collect {
                            when(it) {
                                FishScalePlusAppsFlyerState.FishScalePlusDefault -> {}
                                FishScalePlusAppsFlyerState.FishScalePlusError -> {
                                    fishScalePlusSharedPreference.fishScalePlusAppState = 2
                                    _fishScalePlusHomeScreenState.value =
                                        FishScalePlusHomeScreenState.FishScalePlusError
                                    fishScalePlusGetApps = true
                                }
                                is FishScalePlusAppsFlyerState.FishScalePlusSuccess -> {
                                    if (!fishScalePlusGetApps) {
                                        fishScalePlusGetData(it.fishScalePlusData)
                                        fishScalePlusGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _fishScalePlusHomeScreenState.value =
                            FishScalePlusHomeScreenState.FishScalePlusNotInternet
                    }
                }
                1 -> {
                    if (fishScalePlusSystemService.fishScalePlusIsOnline()) {
                        if (FishScalePlusApplication.FISH_SCALE_PLUS_FB_LI != null) {
                            _fishScalePlusHomeScreenState.value =
                                FishScalePlusHomeScreenState.FishScalePlusSuccess(
                                    FishScalePlusApplication.FISH_SCALE_PLUS_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > fishScalePlusSharedPreference.fishScalePlusExpired) {
                            Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Current time more then expired, repeat request")
                            FishScalePlusApplication.fishScalePlusConversionFlow.collect {
                                when(it) {
                                    FishScalePlusAppsFlyerState.FishScalePlusDefault -> {}
                                    FishScalePlusAppsFlyerState.FishScalePlusError -> {
                                        _fishScalePlusHomeScreenState.value =
                                            FishScalePlusHomeScreenState.FishScalePlusSuccess(
                                                fishScalePlusSharedPreference.fishScalePlusSavedUrl
                                            )
                                        fishScalePlusGetApps = true
                                    }
                                    is FishScalePlusAppsFlyerState.FishScalePlusSuccess -> {
                                        if (!fishScalePlusGetApps) {
                                            fishScalePlusGetData(it.fishScalePlusData)
                                            fishScalePlusGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Current time less then expired, use saved url")
                            _fishScalePlusHomeScreenState.value =
                                FishScalePlusHomeScreenState.FishScalePlusSuccess(
                                    fishScalePlusSharedPreference.fishScalePlusSavedUrl
                                )
                        }
                    } else {
                        _fishScalePlusHomeScreenState.value =
                            FishScalePlusHomeScreenState.FishScalePlusNotInternet
                    }
                }
                2 -> {
                    _fishScalePlusHomeScreenState.value =
                        FishScalePlusHomeScreenState.FishScalePlusError
                }
            }
        }
    }


    private suspend fun fishScalePlusGetData(conversation: MutableMap<String, Any>?) {
        val fishScalePlusData = fishScalePlusGetAllUseCase.invoke(conversation)
        if (fishScalePlusSharedPreference.fishScalePlusAppState == 0) {
            if (fishScalePlusData == null) {
                fishScalePlusSharedPreference.fishScalePlusAppState = 2
                _fishScalePlusHomeScreenState.value =
                    FishScalePlusHomeScreenState.FishScalePlusError
            } else {
                fishScalePlusSharedPreference.fishScalePlusAppState = 1
                fishScalePlusSharedPreference.apply {
                    fishScalePlusExpired = fishScalePlusData.fishScalePlusExpires
                    fishScalePlusSavedUrl = fishScalePlusData.fishScalePlusUrl
                }
                _fishScalePlusHomeScreenState.value =
                    FishScalePlusHomeScreenState.FishScalePlusSuccess(fishScalePlusData.fishScalePlusUrl)
            }
        } else  {
            if (fishScalePlusData == null) {
                _fishScalePlusHomeScreenState.value =
                    FishScalePlusHomeScreenState.FishScalePlusSuccess(fishScalePlusSharedPreference.fishScalePlusSavedUrl)
            } else {
                fishScalePlusSharedPreference.apply {
                    fishScalePlusExpired = fishScalePlusData.fishScalePlusExpires
                    fishScalePlusSavedUrl = fishScalePlusData.fishScalePlusUrl
                }
                _fishScalePlusHomeScreenState.value =
                    FishScalePlusHomeScreenState.FishScalePlusSuccess(fishScalePlusData.fishScalePlusUrl)
            }
        }
    }


    sealed class FishScalePlusHomeScreenState {
        data object FishScalePlusLoading : FishScalePlusHomeScreenState()
        data object FishScalePlusError : FishScalePlusHomeScreenState()
        data class FishScalePlusSuccess(val data: String) : FishScalePlusHomeScreenState()
        data object FishScalePlusNotInternet: FishScalePlusHomeScreenState()
    }
}