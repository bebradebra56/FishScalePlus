package com.fishscal.plisfo.trng

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication

class FishScalePlusGlobalLayoutUtil {

    private var fishScalePlusMChildOfContent: View? = null
    private var fishScalePlusUsableHeightPrevious = 0

    fun fishScalePlusAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        fishScalePlusMChildOfContent = content.getChildAt(0)

        fishScalePlusMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val fishScalePlusUsableHeightNow = fishScalePlusComputeUsableHeight()
        if (fishScalePlusUsableHeightNow != fishScalePlusUsableHeightPrevious) {
            val fishScalePlusUsableHeightSansKeyboard = fishScalePlusMChildOfContent?.rootView?.height ?: 0
            val fishScalePlusHeightDifference = fishScalePlusUsableHeightSansKeyboard - fishScalePlusUsableHeightNow

            if (fishScalePlusHeightDifference > (fishScalePlusUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(FishScalePlusApplication.fishScalePlusInputMode)
            } else {
                activity.window.setSoftInputMode(FishScalePlusApplication.fishScalePlusInputMode)
            }
//            mChildOfContent?.requestLayout()
            fishScalePlusUsableHeightPrevious = fishScalePlusUsableHeightNow
        }
    }

    private fun fishScalePlusComputeUsableHeight(): Int {
        val r = Rect()
        fishScalePlusMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}