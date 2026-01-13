package com.fishscal.plisfo.trng.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fishscal.plisfo.MainActivity
import com.fishscal.plisfo.R
import com.fishscal.plisfo.databinding.FragmentLoadFishScalePlusBinding
import com.fishscal.plisfo.trng.data.shar.FishScalePlusSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FishScalePlusLoadFragment : Fragment(R.layout.fragment_load_fish_scale_plus) {
    private lateinit var fishScalePlusLoadBinding: FragmentLoadFishScalePlusBinding

    private val fishScalePlusLoadViewModel by viewModel<FishScalePlusLoadViewModel>()

    private val fishScalePlusSharedPreference by inject<FishScalePlusSharedPreference>()

    private var fishScalePlusUrl = ""

    private val fishScalePlusRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fishScalePlusNavigateToSuccess(fishScalePlusUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                fishScalePlusSharedPreference.fishScalePlusNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                fishScalePlusNavigateToSuccess(fishScalePlusUrl)
            } else {
                fishScalePlusNavigateToSuccess(fishScalePlusUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fishScalePlusLoadBinding = FragmentLoadFishScalePlusBinding.bind(view)

        fishScalePlusLoadBinding.fishScalePlusGrandButton.setOnClickListener {
            val fishScalePlusPermission = Manifest.permission.POST_NOTIFICATIONS
            fishScalePlusRequestNotificationPermission.launch(fishScalePlusPermission)
            fishScalePlusSharedPreference.fishScalePlusNotificationRequestedBefore = true
        }

        fishScalePlusLoadBinding.fishScalePlusSkipButton.setOnClickListener {
            fishScalePlusSharedPreference.fishScalePlusNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            fishScalePlusNavigateToSuccess(fishScalePlusUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fishScalePlusLoadViewModel.fishScalePlusHomeScreenState.collect {
                    when (it) {
                        is FishScalePlusLoadViewModel.FishScalePlusHomeScreenState.FishScalePlusLoading -> {

                        }

                        is FishScalePlusLoadViewModel.FishScalePlusHomeScreenState.FishScalePlusError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is FishScalePlusLoadViewModel.FishScalePlusHomeScreenState.FishScalePlusSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val fishScalePlusPermission = Manifest.permission.POST_NOTIFICATIONS
                                val fishScalePlusPermissionRequestedBefore = fishScalePlusSharedPreference.fishScalePlusNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), fishScalePlusPermission) == PackageManager.PERMISSION_GRANTED) {
                                    fishScalePlusNavigateToSuccess(it.data)
                                } else if (!fishScalePlusPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > fishScalePlusSharedPreference.fishScalePlusNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    fishScalePlusLoadBinding.fishScalePlusNotiGroup.visibility = View.VISIBLE
                                    fishScalePlusLoadBinding.fishScalePlusLoadingGroup.visibility = View.GONE
                                    fishScalePlusUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(fishScalePlusPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > fishScalePlusSharedPreference.fishScalePlusNotificationRequest) {
                                        fishScalePlusLoadBinding.fishScalePlusNotiGroup.visibility = View.VISIBLE
                                        fishScalePlusLoadBinding.fishScalePlusLoadingGroup.visibility = View.GONE
                                        fishScalePlusUrl = it.data
                                    } else {
                                        fishScalePlusNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    fishScalePlusNavigateToSuccess(it.data)
                                }
                            } else {
                                fishScalePlusNavigateToSuccess(it.data)
                            }
                        }

                        FishScalePlusLoadViewModel.FishScalePlusHomeScreenState.FishScalePlusNotInternet -> {
                            fishScalePlusLoadBinding.fishScalePlusStateGroup.visibility = View.VISIBLE
                            fishScalePlusLoadBinding.fishScalePlusLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun fishScalePlusNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_fishScalePlusLoadFragment_to_fishScalePlusV,
            bundleOf(FISH_SCALE_PLUS_D to data)
        )
    }

    companion object {
        const val FISH_SCALE_PLUS_D = "fishScalePlusData"
    }
}