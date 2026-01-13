package com.fishscal.plisfo.trng.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication

class FishScalePlusPushHandler {
    fun fishScalePlusHandlePush(extras: Bundle?) {
        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = fishScalePlusBundleToMap(extras)
            Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    FishScalePlusApplication.FISH_SCALE_PLUS_FB_LI = map["url"]
                    Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Push data no!")
        }
    }

    private fun fishScalePlusBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}