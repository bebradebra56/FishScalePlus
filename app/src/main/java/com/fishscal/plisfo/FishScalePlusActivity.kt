package com.fishscal.plisfo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication
import com.fishscal.plisfo.trng.FishScalePlusGlobalLayoutUtil
import com.fishscal.plisfo.trng.fishScalePlusSetupSystemBars
import com.fishscal.plisfo.trng.presentation.pushhandler.FishScalePlusPushHandler
import org.koin.android.ext.android.inject

class FishScalePlusActivity : AppCompatActivity() {
    private val fishScalePlusPushHandler by inject<FishScalePlusPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fishScalePlusSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_fish_scale_plus)

        val fishScalePlusRootView = findViewById<View>(android.R.id.content)
        FishScalePlusGlobalLayoutUtil().fishScalePlusAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(fishScalePlusRootView) { fishScalePlusView, fishScalePlusInsets ->
            val fishScalePlusSystemBars = fishScalePlusInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val fishScalePlusDisplayCutout = fishScalePlusInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val fishScalePlusIme = fishScalePlusInsets.getInsets(WindowInsetsCompat.Type.ime())


            val fishScalePlusTopPadding = maxOf(fishScalePlusSystemBars.top, fishScalePlusDisplayCutout.top)
            val fishScalePlusLeftPadding = maxOf(fishScalePlusSystemBars.left, fishScalePlusDisplayCutout.left)
            val fishScalePlusRightPadding = maxOf(fishScalePlusSystemBars.right, fishScalePlusDisplayCutout.right)
            window.setSoftInputMode(FishScalePlusApplication.fishScalePlusInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "ADJUST PUN")
                val fishScalePlusBottomInset = maxOf(fishScalePlusSystemBars.bottom, fishScalePlusDisplayCutout.bottom)

                fishScalePlusView.setPadding(fishScalePlusLeftPadding, fishScalePlusTopPadding, fishScalePlusRightPadding, 0)

                fishScalePlusView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = fishScalePlusBottomInset
                }
            } else {
                Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "ADJUST RESIZE")

                val fishScalePlusBottomInset = maxOf(fishScalePlusSystemBars.bottom, fishScalePlusDisplayCutout.bottom, fishScalePlusIme.bottom)

                fishScalePlusView.setPadding(fishScalePlusLeftPadding, fishScalePlusTopPadding, fishScalePlusRightPadding, 0)

                fishScalePlusView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = fishScalePlusBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Activity onCreate()")
        fishScalePlusPushHandler.fishScalePlusHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            fishScalePlusSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        fishScalePlusSetupSystemBars()
    }
}