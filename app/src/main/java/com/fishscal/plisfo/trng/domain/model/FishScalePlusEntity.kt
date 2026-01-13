package com.fishscal.plisfo.trng.domain.model

import com.google.gson.annotations.SerializedName


data class FishScalePlusEntity (
    @SerializedName("ok")
    val fishScalePlusOk: String,
    @SerializedName("url")
    val fishScalePlusUrl: String,
    @SerializedName("expires")
    val fishScalePlusExpires: Long,
)