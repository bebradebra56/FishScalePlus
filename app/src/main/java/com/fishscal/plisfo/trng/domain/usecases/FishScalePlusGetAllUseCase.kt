package com.fishscal.plisfo.trng.domain.usecases

import android.util.Log
import com.fishscal.plisfo.trng.data.repo.FishScalePlusRepository
import com.fishscal.plisfo.trng.data.utils.FishScalePlusPushToken
import com.fishscal.plisfo.trng.data.utils.FishScalePlusSystemService
import com.fishscal.plisfo.trng.domain.model.FishScalePlusEntity
import com.fishscal.plisfo.trng.domain.model.FishScalePlusParam
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication

class FishScalePlusGetAllUseCase(
    private val fishScalePlusRepository: FishScalePlusRepository,
    private val fishScalePlusSystemService: FishScalePlusSystemService,
    private val fishScalePlusPushToken: FishScalePlusPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : FishScalePlusEntity?{
        val params = FishScalePlusParam(
            fishScalePlusLocale = fishScalePlusSystemService.fishScalePlusGetLocale(),
            fishScalePlusPushToken = fishScalePlusPushToken.fishScalePlusGetToken(),
            fishScalePlusAfId = fishScalePlusSystemService.fishScalePlusGetAppsflyerId()
        )
        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Params for request: $params")
        return fishScalePlusRepository.fishScalePlusGetClient(params, conversion)
    }



}