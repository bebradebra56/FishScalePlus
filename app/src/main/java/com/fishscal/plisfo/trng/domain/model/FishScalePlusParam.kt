package com.fishscal.plisfo.trng.domain.model

import com.google.gson.annotations.SerializedName


private const val FISH_SCALE_PLUS_A = "com.fishscal.plisfo"
private const val FISH_SCALE_PLUS_B = "fishscaleplus"
data class FishScalePlusParam (
    @SerializedName("af_id")
    val fishScalePlusAfId: String,
    @SerializedName("bundle_id")
    val fishScalePlusBundleId: String = FISH_SCALE_PLUS_A,
    @SerializedName("os")
    val fishScalePlusOs: String = "Android",
    @SerializedName("store_id")
    val fishScalePlusStoreId: String = FISH_SCALE_PLUS_A,
    @SerializedName("locale")
    val fishScalePlusLocale: String,
    @SerializedName("push_token")
    val fishScalePlusPushToken: String,
    @SerializedName("firebase_project_id")
    val fishScalePlusFirebaseProjectId: String = FISH_SCALE_PLUS_B,

    )