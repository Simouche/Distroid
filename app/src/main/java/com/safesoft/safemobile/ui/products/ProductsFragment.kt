package com.safesoft.safemobile.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentProductsBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import com.safesoft.safemobile.ui.providers.CreateProviderFragment
import com.safesoft.safemobile.ui.providers.ProvidersListFragment


class ProductsFragment : BaseFragment() {

    lateinit var binding: FragmentProductsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        titles = arrayOf(R.string.products, R.string.create_product)
        fragments = arrayOf(ProductsListFragment(), CreateProductFragment())
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