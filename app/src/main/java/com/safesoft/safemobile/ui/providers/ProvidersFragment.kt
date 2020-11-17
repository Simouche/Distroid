package com.safesoft.safemobile.ui.providers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentProvidersBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import com.safesoft.safemobile.viewmodel.ProvidersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProvidersFragment : BaseFragment() {

    private lateinit var binding: FragmentProvidersBinding
    private val viewModel: ProvidersViewModel by viewModels(this::requireActivity)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_providers, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        titles = arrayOf(R.string.providers, R.string.create_provider)
        fragments = arrayOf(ProvidersListFragment(), CreateProviderFragment())
        pagerAdapter = BaseFragmentAdapter(this, fragments = fragments)
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.page.adapter = pagerAdapter
        TabLayoutMediator(binding.tabs, binding.page) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }


}