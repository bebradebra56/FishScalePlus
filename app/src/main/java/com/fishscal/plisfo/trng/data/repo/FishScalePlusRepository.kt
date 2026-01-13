package com.fishscal.plisfo.trng.data.repo

import android.util.Log
import com.fishscal.plisfo.trng.domain.model.FishScalePlusEntity
import com.fishscal.plisfo.trng.domain.model.FishScalePlusParam
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication.Companion.FISH_SCALE_PLUS_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FishScalePlusApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun fishScalePlusGetClient(
        @Body jsonString: JsonObject,
    ): Call<FishScalePlusEntity>
}


private const val FISH_SCALE_PLUS_MAIN = "https://fishscaleplus.com/"
class FishScalePlusRepository {

    suspend fun fishScalePlusGetClient(
        fishScalePlusParam: FishScalePlusParam,
        fishScalePlusConversion: MutableMap<String, Any>?
    ): FishScalePlusEntity? {
        val gson = Gson()
        val api = fishScalePlusGetApi(FISH_SCALE_PLUS_MAIN, null)

        val fishScalePlusJsonObject = gson.toJsonTree(fishScalePlusParam).asJsonObject
        fishScalePlusConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            fishScalePlusJsonObject.add(key, element)
        }
        return try {
            val fishScalePlusRequest: Call<FishScalePlusEntity> = api.fishScalePlusGetClient(
                jsonString = fishScalePlusJsonObject,
            )
            val fishScalePlusResult = fishScalePlusRequest.awaitResponse()
            Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Retrofit: Result code: ${fishScalePlusResult.code()}")
            if (fishScalePlusResult.code() == 200) {
                Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Retrofit: Get request success")
                Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Retrofit: Code = ${fishScalePlusResult.code()}")
                Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Retrofit: ${fishScalePlusResult.body()}")
                fishScalePlusResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(FISH_SCALE_PLUS_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun fishScalePlusGetApi(url: String, client: OkHttpClient?) : FishScalePlusApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
