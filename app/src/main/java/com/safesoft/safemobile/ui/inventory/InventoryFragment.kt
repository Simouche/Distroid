package com.safesoft.safemobile.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentInventoryBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryFragment : BaseFragment() {

    private lateinit var binding: FragmentInventoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        titles = arrayOf(R.string.inventories, R.string.create_inventory)
        fragments = arrayOf(InventoriesListFragment(), CreateInventoryFragment())
        pagerAdapter = BaseFragmentAdapter(this, fragments = fragments)
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.pages.adapter = pagerAdapter
        TabLayoutMediator(binding.tabs, binding.pages) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }

}