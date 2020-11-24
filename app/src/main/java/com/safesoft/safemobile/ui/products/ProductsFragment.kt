package com.safesoft.safemobile.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentProductsBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.BaseFragmentAdapter
import com.safesoft.safemobile.viewmodel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : BaseFragment() {

    lateinit var binding: FragmentProductsBinding

    private val viewModel: ProductsViewModel by viewModels(this::requireActivity)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false)
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        val operation =
            if (viewModel.operation == 0) R.string.create_product else R.string.update_product
        titles = arrayOf(R.string.products, operation)
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

    override fun setUpObservers() {
        super.setUpObservers()
        if (binding.page.currentItem != viewModel.page)
            binding.page.currentItem = viewModel.page
    }
}