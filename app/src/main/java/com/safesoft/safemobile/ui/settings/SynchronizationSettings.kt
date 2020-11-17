package com.safesoft.safemobile.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.worker.ClientsWorker
import com.safesoft.safemobile.databinding.FragmentSynchronizationSettingsBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SynchronizationSettings : BaseFragment() {

    private lateinit var binding: FragmentSynchronizationSettingsBinding

    private val viewModel: SettingsViewModel by viewModels(this::requireActivity)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_synchronization_settings,
            container,
            false
        )
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

    }

    override fun setUpObservers() {
        super.setUpObservers()
        binding.syncFab.setOnClickListener {
            val request = OneTimeWorkRequestBuilder<ClientsWorker>().build()
            WorkManager.getInstance(requireContext()).enqueue(request)
        }
    }

}