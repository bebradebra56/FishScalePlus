package com.fishscal.plisfo.trng.data.shar

import android.content.Context
import androidx.core.content.edit

class FishScalePlusSharedPreference(context: Context) {
    private val fishScalePlusPrefs = context.getSharedPreferences("fishScalePlusSharedPrefsAb", Context.MODE_PRIVATE)

    var fishScalePlusSavedUrl: String
        get() = fishScalePlusPrefs.getString(FISH_SCALE_PLUS_SAVED_URL, "") ?: ""
        set(value) = fishScalePlusPrefs.edit { putString(FISH_SCALE_PLUS_SAVED_URL, value) }

    var fishScalePlusExpired : Long
        get() = fishScalePlusPrefs.getLong(FISH_SCALE_PLUS_EXPIRED, 0L)
        set(value) = fishScalePlusPrefs.edit { putLong(FISH_SCALE_PLUS_EXPIRED, value) }

    var fishScalePlusAppState: Int
        get() = fishScalePlusPrefs.getInt(FISH_SCALE_PLUS_APPLICATION_STATE, 0)
        set(value) = fishScalePlusPrefs.edit { putInt(FISH_SCALE_PLUS_APPLICATION_STATE, value) }

    var fishScalePlusNotificationRequest: Long
        get() = fishScalePlusPrefs.getLong(FISH_SCALE_PLUS_NOTIFICAITON_REQUEST, 0L)
        set(value) = fishScalePlusPrefs.edit { putLong(FISH_SCALE_PLUS_NOTIFICAITON_REQUEST, value) }

    var fishScalePlusNotificationRequestedBefore: Boolean
        get() = fishScalePlusPrefs.getBoolean(FISH_SCALE_PLUS_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = fishScalePlusPrefs.edit { putBoolean(
            FISH_SCALE_PLUS_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val FISH_SCALE_PLUS_SAVED_URL = "fishScalePlusSavedUrl"
        private const val FISH_SCALE_PLUS_EXPIRED = "fishScalePlusExpired"
        private const val FISH_SCALE_PLUS_APPLICATION_STATE = "fishScalePlusApplicationState"
        private const val FISH_SCALE_PLUS_NOTIFICAITON_REQUEST = "fishScalePlusNotificationRequest"
        private const val FISH_SCALE_PLUS_NOTIFICATION_REQUEST_BEFORE = "fishScalePlusNotificationRequestedBefore"
    }
}