package com.safesoft.safemobile.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentSettingsBinding
import com.safesoft.safemobile.ui.clients.ClientsListFragment
import com.safesoft.safemobile.ui.clients.CreateClientFragment
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        titles = arrayOf(R.string.synchronization, R.string.application, R.string.users)
        fragments = arrayOf(SynchronizationSettings(), ApplicationSettings(), UserSettings())
        pagerAdapter = BaseFragmentAdapter(this, fragments)
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.pages.adapter = pagerAdapter
        TabLayoutMediator(binding.tabs, binding.pages) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }

}