package com.safesoft.safemobile.ui.purchases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentGalleryBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchasesFragment : BaseFragment() {

    private lateinit var binding: FragmentGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        titles = arrayOf(R.string.purchases, R.string.create_purchase)
        fragments = arrayOf(PurchasesListFragment(), CreatePurchaseFragment())
        pagerAdapter = BaseFragmentAdapter(this, fragments = fragments)
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.page.adapter = pagerAdapter
        TabLayoutMediator(binding.pages, binding.page) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }


}