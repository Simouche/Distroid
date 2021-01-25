package com.safesoft.safemobile.ui.inventory

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAProduct
import com.safesoft.safemobile.databinding.FragmentCreateInventoryBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.BaseScannerFragment
import com.safesoft.safemobile.ui.generics.adapter.GenericSpinnerAdapter
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.onTextChanged
import com.safesoft.safemobile.ui.products.ProductsListFragment
import com.safesoft.safemobile.viewmodel.InventoryViewModel
import com.safesoft.safemobile.viewmodel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateInventoryFragment : BaseScannerFragment() {
    private lateinit var binding: FragmentCreateInventoryBinding


    private val viewModel: InventoryViewModel by viewModels(this::requireActivity)
    private val productsViewModel: ProductsViewModel by viewModels(this::requireActivity)

    @Inject
    lateinit var recyclerAdapter: InventoryLinesRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_inventory, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        recyclerAdapter.deleter =
            OnItemClickListener { position, view -> deleteLine(position, view) }
        binding.inventorySelectedProductsList.adapter = recyclerAdapter
        binding.viewModel = viewModel

        binding.validate.setOnClickListener { validate() }
        setUpProductSearch()
    }

    private fun setUpProductSearch() {
        val initItems = mutableListOf<AllAboutAProduct>()
        val adapter = GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, initItems)
        adapter.setNotifyOnChange(true)
        binding.inventorySelectProduct.setOnItemClickListener { _, _, i, _ ->
            Log.d(TAG, "setUpProductSearch: product selected is ${adapter.getItem(i).toString()}")
            binding.inventorySelectProduct.setText("")
            ProductsListFragment.showProductDetailsDialog(
                requireContext(),
                adapter.getItem(i),
                false,
                this::addLine
            )
            adapter.objects = mutableListOf()
            adapter.notifyDataSetChanged()
        }
        binding.inventorySelectProduct.onTextChanged { s ->
            Log.d(TAG, "setUpProductSearch: searching for $s")

            if (productsViewModel.searchBrandFlow(s).hasActiveObservers())
                productsViewModel.searchBrandFlow(s).removeObservers(viewLifecycleOwner)

            productsViewModel.searchFlow(s).observe(viewLifecycleOwner, Observer {
                when (it.state) {
                    loading -> return@Observer
                    success -> {
                        adapter.objects = it.data!!
                        adapter.notifyDataSetChanged()
                    }
                    error -> return@Observer
                }
            }
            )
        }
        binding.inventorySelectProduct.setAdapter(adapter)
        binding.productIcon.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_inventory_to_nav_create_product))
        binding.inventorySelectProduct.setOnLongClickListener {
            launchScanner()
            true
        }
    }

    override fun handleScannerResult(text: String) {
        super.handleScannerResult(text)
        if (text.isEmpty()) return
        isScanning = false
        binding.inventorySelectProduct.setText(text)
        Log.d(TAG, "handleScannerResult: finished handling the scan result")
    }

    private fun addLine(product: AllAboutAProduct?, quantity: Double) {
        recyclerAdapter.addItem(viewModel.addLine(product, quantity))
    }

    private fun deleteLine(position: Int, view: View?) {
        val anim: Animation = AnimationUtils.loadAnimation(
            context,
            android.R.anim.slide_out_right
        )
        anim.duration = 300
        Handler().postDelayed({
            view?.startAnimation(anim)
            viewModel.removeLine(recyclerAdapter.items.removeAt(position))
            recyclerAdapter.notifyDataSetChanged()
        }, anim.duration)
    }

    private fun validate() {
        saveInventory()
    }

    private fun saveInventory() {
        viewModel.saveInventory().observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> saveLines(inventory = it.data!!)
                error -> {
                    it.exception?.printStackTrace()
                    toast(R.string.unkown_error)
                }
            }
        })
    }

    private fun saveLines(inventory: Long) {
        val items = recyclerAdapter.items.map { it.copy(inventory = inventory) }
        viewModel.saveLines(items).observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> toast(R.string.inventory_added)
                error -> {
                    it.exception?.printStackTrace()
                    toast(R.string.unkown_error)
                }
            }
        })
    }


}