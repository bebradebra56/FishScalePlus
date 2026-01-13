package com.fishscal.plisfo.trng.data.utils

import android.util.Log
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FishScalePlusPushToken {

    suspend fun fishScalePlusGetToken(
        fishScalePlusMaxAttempts: Int = 3,
        fishScalePlusDelayMs: Long = 1500
    ): String {

        repeat(fishScalePlusMaxAttempts - 1) {
            try {
                val fishScalePlusToken = FirebaseMessaging.getInstance().token.await()
                return fishScalePlusToken
            } catch (e: Exception) {
                Log.e(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(fishScalePlusDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}