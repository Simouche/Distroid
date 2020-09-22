package com.safesoft.safemobile.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentSalesBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SalesFragment : BaseFragment() {

    private lateinit var  binding : FragmentSalesBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        titles = arrayOf(R.string.sales, R.string.create_sale)
        fragments = arrayOf(SalesListFragment(), CreateSaleFragment())
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