package com.fishscal.plisfo.trng.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication
import com.fishscal.plisfo.trng.presentation.ui.load.FishScalePlusLoadFragment
import org.koin.android.ext.android.inject

class FishScalePlusV : Fragment(){

    private lateinit var fishScalePlusPhoto: Uri
    private var fishScalePlusFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val fishScalePlusTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        fishScalePlusFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        fishScalePlusFilePathFromChrome = null
    }

    private val fishScalePlusTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            fishScalePlusFilePathFromChrome?.onReceiveValue(arrayOf(fishScalePlusPhoto))
            fishScalePlusFilePathFromChrome = null
        } else {
            fishScalePlusFilePathFromChrome?.onReceiveValue(null)
            fishScalePlusFilePathFromChrome = null
        }
    }

    private val fishScalePlusDataStore by activityViewModels<FishScalePlusDataStore>()


    private val fishScalePlusViFun by inject<FishScalePlusViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (fishScalePlusDataStore.fishScalePlusView.canGoBack()) {
                        fishScalePlusDataStore.fishScalePlusView.goBack()
                        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "WebView can go back")
                    } else if (fishScalePlusDataStore.fishScalePlusViList.size > 1) {
                        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "WebView can`t go back")
                        fishScalePlusDataStore.fishScalePlusViList.removeAt(fishScalePlusDataStore.fishScalePlusViList.lastIndex)
                        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "WebView list size ${fishScalePlusDataStore.fishScalePlusViList.size}")
                        fishScalePlusDataStore.fishScalePlusView.destroy()
                        val previousWebView = fishScalePlusDataStore.fishScalePlusViList.last()
                        fishScalePlusAttachWebViewToContainer(previousWebView)
                        fishScalePlusDataStore.fishScalePlusView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fishScalePlusDataStore.fishScalePlusIsFirstCreate) {
            fishScalePlusDataStore.fishScalePlusIsFirstCreate = false
            fishScalePlusDataStore.fishScalePlusContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return fishScalePlusDataStore.fishScalePlusContainerView
        } else {
            return fishScalePlusDataStore.fishScalePlusContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "onViewCreated")
        if (fishScalePlusDataStore.fishScalePlusViList.isEmpty()) {
            fishScalePlusDataStore.fishScalePlusView = FishScalePlusVi(requireContext(), object :
                FishScalePlusCallBack {
                override fun fishScalePlusHandleCreateWebWindowRequest(fishScalePlusVi: FishScalePlusVi) {
                    fishScalePlusDataStore.fishScalePlusViList.add(fishScalePlusVi)
                    Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "WebView list size = ${fishScalePlusDataStore.fishScalePlusViList.size}")
                    Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "CreateWebWindowRequest")
                    fishScalePlusDataStore.fishScalePlusView = fishScalePlusVi
                    fishScalePlusVi.fishScalePlusSetFileChooserHandler { callback ->
                        fishScalePlusHandleFileChooser(callback)
                    }
                    fishScalePlusAttachWebViewToContainer(fishScalePlusVi)
                }

            }, fishScalePlusWindow = requireActivity().window).apply {
                fishScalePlusSetFileChooserHandler { callback ->
                    fishScalePlusHandleFileChooser(callback)
                }
            }
            fishScalePlusDataStore.fishScalePlusView.fishScalePlusFLoad(arguments?.getString(
                FishScalePlusLoadFragment.FISH_SCALE_PLUS_D) ?: "")
//            ejvview.fLoad("www.google.com")
            fishScalePlusDataStore.fishScalePlusViList.add(fishScalePlusDataStore.fishScalePlusView)
            fishScalePlusAttachWebViewToContainer(fishScalePlusDataStore.fishScalePlusView)
        } else {
            fishScalePlusDataStore.fishScalePlusViList.forEach { webView ->
                webView.fishScalePlusSetFileChooserHandler { callback ->
                    fishScalePlusHandleFileChooser(callback)
                }
            }
            fishScalePlusDataStore.fishScalePlusView = fishScalePlusDataStore.fishScalePlusViList.last()

            fishScalePlusAttachWebViewToContainer(fishScalePlusDataStore.fishScalePlusView)
        }
        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "WebView list size = ${fishScalePlusDataStore.fishScalePlusViList.size}")
    }

    private fun fishScalePlusHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        fishScalePlusFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Launching file picker")
                    fishScalePlusTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Launching camera")
                    fishScalePlusPhoto = fishScalePlusViFun.fishScalePlusSavePhoto()
                    fishScalePlusTakePhoto.launch(fishScalePlusPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                fishScalePlusFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun fishScalePlusAttachWebViewToContainer(w: FishScalePlusVi) {
        fishScalePlusDataStore.fishScalePlusContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            fishScalePlusDataStore.fishScalePlusContainerView.removeAllViews()
            fishScalePlusDataStore.fishScalePlusContainerView.addView(w)
        }
    }


}