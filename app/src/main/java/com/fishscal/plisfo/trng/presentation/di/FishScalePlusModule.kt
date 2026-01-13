package com.fishscal.plisfo.trng.presentation.di

import com.fishscal.plisfo.trng.data.repo.FishScalePlusRepository
import com.fishscal.plisfo.trng.data.shar.FishScalePlusSharedPreference
import com.fishscal.plisfo.trng.data.utils.FishScalePlusPushToken
import com.fishscal.plisfo.trng.data.utils.FishScalePlusSystemService
import com.fishscal.plisfo.trng.domain.usecases.FishScalePlusGetAllUseCase
import com.fishscal.plisfo.trng.presentation.pushhandler.FishScalePlusPushHandler
import com.fishscal.plisfo.trng.presentation.ui.load.FishScalePlusLoadViewModel
import com.fishscal.plisfo.trng.presentation.ui.view.FishScalePlusViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val fishScalePlusModule = module {
    factory {
        FishScalePlusPushHandler()
    }
    single {
        FishScalePlusRepository()
    }
    single {
        FishScalePlusSharedPreference(get())
    }
    factory {
        FishScalePlusPushToken()
    }
    factory {
        FishScalePlusSystemService(get())
    }
    factory {
        FishScalePlusGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        FishScalePlusViFun(get())
    }
    viewModel {
        FishScalePlusLoadViewModel(get(), get(), get())
    }
}