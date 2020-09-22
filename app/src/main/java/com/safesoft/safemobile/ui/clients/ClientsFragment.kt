package com.safesoft.safemobile.ui.clients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentClientsBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import com.safesoft.safemobile.ui.providers.CreateProviderFragment


class ClientsFragment : BaseFragment() {

    private lateinit var binding: FragmentClientsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clients, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        titles = arrayOf(R.string.clients, R.string.create_client)
        fragments = arrayOf(ClientsListFragment(), CreateClientFragment())
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