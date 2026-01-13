package com.fishscal.plisfo.trng.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class FishScalePlusDataStore : ViewModel(){
    val fishScalePlusViList: MutableList<FishScalePlusVi> = mutableListOf()
    var fishScalePlusIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var fishScalePlusContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var fishScalePlusView: FishScalePlusVi

}