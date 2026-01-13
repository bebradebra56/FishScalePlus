package com.fishscal.plisfo.trng.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import androidx.room.Room
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.fishscal.plisfo.data.datastore.PreferencesManager
import com.fishscal.plisfo.data.local.FishDatabase
import com.fishscal.plisfo.data.repository.FishRepository
import com.fishscal.plisfo.trng.presentation.di.fishScalePlusModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface FishScalePlusAppsFlyerState {
    data object FishScalePlusDefault : FishScalePlusAppsFlyerState
    data class FishScalePlusSuccess(val fishScalePlusData: MutableMap<String, Any>?) :
        FishScalePlusAppsFlyerState

    data object FishScalePlusError : FishScalePlusAppsFlyerState
}

interface FishScalePlusAppsApi {
    @Headers("Content-Type: application/json")
    @GET(FISH_SCALE_PLUS_LIN)
    fun fishScalePlusGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val FISH_SCALE_PLUS_APP_DEV = "hDS9sA9YGpxP9Vky7hUDjn"
private const val FISH_SCALE_PLUS_LIN = "com.fishscal.plisfo"

class FishScalePlusApplication : Application() {


    lateinit var database: FishDatabase
        private set

    lateinit var repository: FishRepository
        private set

    lateinit var preferencesManager: PreferencesManager
        private set


    private var fishScalePlusIsResumed = false
//    private var fishScalePlusConversionTimeoutJob: Job? = null
    private var fishScalePlusDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()


        database = Room.databaseBuilder(
            applicationContext,
            FishDatabase::class.java,
            "fish_scale_database"
        ).build()

        repository = FishRepository(database.fishCatchDao())
        preferencesManager = PreferencesManager(applicationContext)

        val appsflyer = AppsFlyerLib.getInstance()
        fishScalePlusSetDebufLogger(appsflyer)
        fishScalePlusMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        fishScalePlusExtractDeepMap(p0.deepLink)
                        Log.d(FISH_SCALE_PLUS_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(FISH_SCALE_PLUS_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(FISH_SCALE_PLUS_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            FISH_SCALE_PLUS_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
//                    fishScalePlusConversionTimeoutJob?.cancel()
                    Log.d(FISH_SCALE_PLUS_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = fishScalePlusGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.fishScalePlusGetClient(
                                    devkey = FISH_SCALE_PLUS_APP_DEV,
                                    deviceId = fishScalePlusGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(FISH_SCALE_PLUS_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    fishScalePlusResume(
                                        FishScalePlusAppsFlyerState.FishScalePlusSuccess(
                                            p0
                                        )
                                    )
                                } else {
                                    fishScalePlusResume(
                                        FishScalePlusAppsFlyerState.FishScalePlusSuccess(
                                            resp
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Error: ${d.message}")
                                fishScalePlusResume(FishScalePlusAppsFlyerState.FishScalePlusError)
                            }
                        }
                    } else {
                        fishScalePlusResume(FishScalePlusAppsFlyerState.FishScalePlusSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
//                    fishScalePlusConversionTimeoutJob?.cancel()
                    Log.d(FISH_SCALE_PLUS_MAIN_TAG, "onConversionDataFail: $p0")
                    fishScalePlusResume(FishScalePlusAppsFlyerState.FishScalePlusError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(FISH_SCALE_PLUS_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(FISH_SCALE_PLUS_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, FISH_SCALE_PLUS_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(FISH_SCALE_PLUS_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(FISH_SCALE_PLUS_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
//        fishScalePlusStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@FishScalePlusApplication)
            modules(
                listOf(
                    fishScalePlusModule
                )
            )
        }
    }

    private fun fishScalePlusExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Extracted DeepLink data: $map")
        fishScalePlusDeepLinkData = map
    }

//    private fun fishScalePlusStartConversionTimeout() {
//        fishScalePlusConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
//            delay(30000)
//            if (!fishScalePlusIsResumed) {
//                Log.d(PLINK_ZEN_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
//                fishScalePlusResume(PlinkZenAppsFlyerState.PlinkZenError)
//            }
//        }
//    }

    private fun fishScalePlusResume(state: FishScalePlusAppsFlyerState) {
//        fishScalePlusConversionTimeoutJob?.cancel()
        if (state is FishScalePlusAppsFlyerState.FishScalePlusSuccess) {
            val convData = state.fishScalePlusData ?: mutableMapOf()
            val deepData = fishScalePlusDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!fishScalePlusIsResumed) {
                fishScalePlusIsResumed = true
                fishScalePlusConversionFlow.value =
                    FishScalePlusAppsFlyerState.FishScalePlusSuccess(merged)
            }
        } else {
            if (!fishScalePlusIsResumed) {
                fishScalePlusIsResumed = true
                fishScalePlusConversionFlow.value = state
            }
        }
    }

    private fun fishScalePlusGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(FISH_SCALE_PLUS_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun fishScalePlusSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun fishScalePlusMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun fishScalePlusGetApi(url: String, client: OkHttpClient?): FishScalePlusAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {

        var fishScalePlusInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val fishScalePlusConversionFlow: MutableStateFlow<FishScalePlusAppsFlyerState> = MutableStateFlow(
            FishScalePlusAppsFlyerState.FishScalePlusDefault
        )
        var FISH_SCALE_PLUS_FB_LI: String? = null
        const val FISH_SCALE_PLUS_MAIN_TAG = "FishScalePlusMainTag"
    }
}