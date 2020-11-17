package com.safesoft.safemobile.ui.products

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.utils.doubleValue
import com.safesoft.safemobile.databinding.DialogSelectProductBinding
import com.safesoft.safemobile.databinding.FragmentProductsListBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.addDivider
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.viewmodel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductsListFragment : BaseFragment() {

    private val productsViewModel: ProductsViewModel by viewModels(this::requireActivity)
    private lateinit var binding: FragmentProductsListBinding

    @Inject
    lateinit var recyclerAdapter: ProductsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_products_list, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = productsViewModel
        binding.productsList.adapter = recyclerAdapter
        binding.productsList.addDivider()
        binding.productsSearchField.requestFocus()
        recyclerAdapter.onNormalClickListener = OnItemClickListener { position, _ ->
            showProductDetailsDialog(requireContext(), recyclerAdapter.getItemAt(position))
        }
    }

    override fun setUpObservers() {
        super.setUpObservers()
        if (!productsViewModel.productsList.hasActiveObservers() && (!productsViewModel.searchQuery.hasActiveObservers()))
            productsViewModel.productsList.observe(
                viewLifecycleOwner,
                Observer {
                    Log.d(TAG, "setUpObservers: All Selected")
                    recyclerAdapter.submitList(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            )

        if (!productsViewModel.searchQuery.hasActiveObservers())
            productsViewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
                if (query.isBlank()) {
                    Log.d(TAG, "setUpObservers: query is blank")
                    if (!productsViewModel.productsList.hasActiveObservers())
                        productsViewModel.productsList.observe(
                            viewLifecycleOwner,
                            Observer {
                                recyclerAdapter.submitList(it)
                                recyclerAdapter.notifyDataSetChanged()
                            }
                        )
                } else {
                    Log.d(TAG, "setUpObservers: query isn't blank")
                    if (productsViewModel.productsList.hasActiveObservers()) productsViewModel.productsList.removeObservers(
                        viewLifecycleOwner
                    )
                    productsViewModel.search(query).observe(
                        viewLifecycleOwner,
                        Observer { it ->
                            Log.d(
                                TAG,
                                "setUpObservers: searching query of $query result is " + it.size
                            )
                            recyclerAdapter.submitList(it)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    )
                }
            }
            )
    }

    companion object {
        fun showProductDetailsDialog(
            context: Context,
            product: AllAboutAProduct?,
            detailsMode: Boolean = true,
            callback: (product: AllAboutAProduct?, quantity: Double) -> Unit = { _: AllAboutAProduct?, _: Double ->
                Log.d(
                    DialogSelectProductBinding::class.simpleName,
                    "showProductDetailsDialog: NoCallBackProvided"
                )
            }
        ) {
            val binding: DialogSelectProductBinding = DataBindingUtil
                .inflate(LayoutInflater.from(context), R.layout.dialog_select_product, null, false)
            binding.product = product
            binding.detailMode = detailsMode

            val dialog =
                AlertDialog.Builder(context).setView(binding.root).setCancelable(true)
                    .create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            binding.addProductButton.setOnClickListener {
                callback.invoke(product, binding.quantity.text.toString().doubleValue())
                dialog.dismiss()
            }
        }
    }

}