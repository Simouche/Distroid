package com.safesoft.safemobile.ui.sales

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.db.entity.Clients
import com.safesoft.safemobile.backend.utils.doubleValue
import com.safesoft.safemobile.databinding.DialogDiscountInputSaleBinding
import com.safesoft.safemobile.databinding.FragmentCreateSaleBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.GenericSpinnerAdapter
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.onTextChanged
import com.safesoft.safemobile.ui.products.ProductsListFragment
import com.safesoft.safemobile.viewmodel.ClientsViewModel
import com.safesoft.safemobile.viewmodel.ProductsViewModel
import com.safesoft.safemobile.viewmodel.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateSaleFragment : BaseFragment() {

    private val TAG = CreateSaleFragment::class.simpleName
    private lateinit var binding: FragmentCreateSaleBinding


    private val viewModel: SalesViewModel by viewModels(this::requireActivity)
    private val clientsViewModel: ClientsViewModel by viewModels(this::requireActivity)
    private val productsViewModel: ProductsViewModel by viewModels(this::requireActivity)

    @Inject
    lateinit var recyclerAdapter: SaleLinesRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_sale, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        recyclerAdapter.deleter =
            OnItemClickListener { position, view -> deleteLine(position, view) }
        binding.selectedProductsList.adapter = recyclerAdapter
        binding.footer.viewModel = viewModel
        binding.footer.lifecycleOwner = viewLifecycleOwner
        setUpProductSearch()
        setUpClientSearch()
    }

    override fun setUpObservers() {
        super.setUpObservers()
        binding.footer.stampSwitcher.setOnCheckedChangeListener { _, b ->
            if (b)
                viewModel.setStamp()
            else
                viewModel.removeStamp()
        }
    }

    private fun setUpClientSearch() {
        val initItems = mutableListOf<Clients>()
        val adapter = GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, initItems)
        adapter.setNotifyOnChange(true)
        binding.saleSelectClient.setOnItemClickListener { _, _, i, _ ->
            Log.d(TAG, "setUpProviderSearch: ")
            viewModel.salesForm.fields.client.value = adapter.getItem(i)?.id
        }
        binding.saleSelectClient.onTextChanged { s ->
            clientsViewModel.searchFlow(s).observe(viewLifecycleOwner, Observer {
                when (it.state) {
                    loading -> return@Observer
                    success -> {
                        adapter.objects = it.data!!
                        adapter.notifyDataSetChanged()
                    }
                    error -> return@Observer
                }
            })
        }
        binding.saleSelectClient.setAdapter(adapter)
    }

    private fun setUpProductSearch() {
        val initItems = mutableListOf<AllAboutAProduct>()
        val adapter = GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, initItems)
        adapter.setNotifyOnChange(true)
        binding.saleSelectProduct.setOnItemClickListener { _, _, i, _ ->
            Log.d(TAG, "setUpProductSearch: product selected is ${adapter.getItem(i).toString()}")
            binding.saleSelectProduct.setText("")
            ProductsListFragment.showProductDetailsDialog(
                requireContext(),
                adapter.getItem(i),
                false,
                this::addLine
            )
            adapter.objects = mutableListOf()
            adapter.notifyDataSetChanged()
        }
        binding.saleSelectProduct.onTextChanged { s ->
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
        binding.saleSelectProduct.setAdapter(adapter)
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

    private fun showDiscountDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: DialogDiscountInputSaleBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.dialog_discount_input_sale, null, false)
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        fBinding.discountAmount.onTextChanged { s ->
            viewModel.discount(s.doubleValue())
        }
        fBinding.closeDiscountAmount.setOnClickListener { dialog.dismiss() }
    }


}