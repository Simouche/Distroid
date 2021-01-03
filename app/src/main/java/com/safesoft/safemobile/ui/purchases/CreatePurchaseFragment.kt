package com.safesoft.safemobile.ui.purchases

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
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.db.local.entity.Providers
import com.safesoft.safemobile.backend.utils.doubleValue
import com.safesoft.safemobile.backend.utils.formatted
import com.safesoft.safemobile.databinding.DialogDiscountInputBinding
import com.safesoft.safemobile.databinding.FragmentCreatePurchaseBinding
import com.safesoft.safemobile.databinding.InvoiceConfirmationDialogBinding
import com.safesoft.safemobile.ui.generics.BaseFormOwner
import com.safesoft.safemobile.ui.generics.BaseScannerFragment
import com.safesoft.safemobile.ui.generics.adapter.GenericSpinnerAdapter
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.onTextChanged
import com.safesoft.safemobile.ui.products.ProductCalculator
import com.safesoft.safemobile.ui.products.ProductsListFragment
import com.safesoft.safemobile.viewmodel.ProductsViewModel
import com.safesoft.safemobile.viewmodel.ProvidersViewModel
import com.safesoft.safemobile.viewmodel.PurchasesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreatePurchaseFragment : BaseScannerFragment(), ProductCalculator, BaseFormOwner {
    private lateinit var binding: FragmentCreatePurchaseBinding

    private var scanRequestedFrom = 0
    private val viewModel: PurchasesViewModel by viewModels(this::requireActivity)
    private val providersViewModel: ProvidersViewModel by viewModels(this::requireActivity)
    private val productsViewModel: ProductsViewModel by viewModels(this::requireActivity)

    @Inject
    lateinit var recyclerAdapter: PurchaseLinesRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_purchase, container, false)
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
//        binding.footer.totalDiscountFooterText.setOnClickListener { showDiscountDialog() }
        binding.footer.totalDiscountFooterText.visibility = View.INVISIBLE
        binding.footer.discountLabel.visibility = View.INVISIBLE
        binding.validate.setOnClickListener { showValidationDialog() }
        setUpProductSearch()
        setUpProviderSearch()
    }

    override fun handleScannerResult(text: String) {
        super.handleScannerResult(text)
        if (text.isEmpty()) return
        if (scanRequestedFrom == 0)
            binding.purchaseSelectProduct.setText(text)
        else
            binding.purchaseSelectProvider.setText(text)
        isScanning = false
        Log.d(TAG, "handleScannerResult: finished handling the scan result")
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

    private fun setUpProviderSearch() {
        binding.purchaseSelectProvider.setText(viewModel.providerName)
        val initItems = mutableListOf<Providers>()
        val adapter = GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, initItems)
        adapter.setNotifyOnChange(true)
        binding.purchaseSelectProvider.setOnItemClickListener { _, _, i, _ ->
            Log.d(TAG, "selected provider: ${adapter.getItem(i)}")
            viewModel.purchaseForm.fields.provider.value = adapter.getItem(i)?.id
            viewModel.provider = adapter.getItem(i)!!.id
        }
        binding.purchaseSelectProvider.onTextChanged { s ->
            viewModel.providerName = s
            providersViewModel.searchFlow(s).observe(viewLifecycleOwner, Observer {
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
        binding.purchaseSelectProvider.setAdapter(adapter)
        binding.providerIcon.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_purchases_to_nac_create_provider))
        binding.purchaseSelectProvider.setOnLongClickListener {
            launchScanner()
            scanRequestedFrom = 1
            true
        }
    }

    private fun setUpProductSearch() {
        val initItems = mutableListOf<AllAboutAProduct>()
        val adapter = GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, initItems)
        adapter.setNotifyOnChange(true)
        binding.purchaseSelectProduct.setOnItemClickListener { _, _, i, _ ->
            Log.d(TAG, "setUpProductSearch: product selected is ${adapter.getItem(i).toString()}")
            binding.purchaseSelectProduct.setText("")
            ProductsListFragment.showProductDetailsDialog(
                requireContext(),
                adapter.getItem(i),
                false,
                this::addLine
            )
            adapter.objects = mutableListOf()
            adapter.notifyDataSetChanged()
        }
        binding.purchaseSelectProduct.onTextChanged { s ->
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
        binding.purchaseSelectProduct.setAdapter(adapter)
        binding.productIcon.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_purchases_to_nav_create_product))
        binding.purchaseSelectProduct.setOnLongClickListener {
            launchScanner()
            scanRequestedFrom = 0
            true
        }
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
        val fBinding: DialogDiscountInputBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.dialog_discount_input, null, false)
        fBinding.viewModel = providersViewModel
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        fBinding.discountAmount.onTextChanged { s ->
            viewModel.discount(s.filter { it.isDigit() }.doubleValue())
        }
        fBinding.closeDiscountAmount.setOnClickListener { dialog.dismiss() }
    }

    private fun showValidationDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: InvoiceConfirmationDialogBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(context),
                R.layout.invoice_confirmation_dialog,
                null,
                false
            )
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        fBinding.totalAmoutTextView.text = getString(
            R.string.total_amount,
            viewModel.invoice.value?.totalTTC?.formatted()
        )
        fBinding.cashRest.setText(viewModel.invoice.value?.totalTTC?.formatted())
        fBinding.cashRest.isEnabled = false
        fBinding.paymentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                fBinding.cashPayment.isEnabled = p2 == 0
                viewModel.paymentType = when (p2) {
                    0 -> "C"
                    1 -> "T"
                    else -> "C"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: nothing selected")
            }
        }
        fBinding.cashPayment.setText(0.0.formatted())
        fBinding.cashPayment.onTextChanged { s ->
            if (fBinding.cashPayment.isFocused) {
                fBinding.cashRest.setText((viewModel.invoice.value!!.totalTTC!! - s.doubleValue()).formatted())
                viewModel.setPayment(s.doubleValue())
            }
        }

        fBinding.invoiceConfirmationDialogConfirmationButton.setOnClickListener {
            saveInvoice()
            dialog.dismiss()
        }
    }

    private fun saveInvoice() {
        viewModel.saveInvoice().observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> saveLines(invoice = it.data!!)
                error -> {
                    it.exception?.printStackTrace()
                    toast(R.string.unkown_error)
                }
            }
        })
    }

    private fun saveLines(invoice: Long) {
        val items = recyclerAdapter.items.map { it.copy(purchase = invoice) }
        viewModel.saveLines(items).observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> {
                    toast(R.string.purchase_added)
                    viewModel
                }
                error -> {
                    it.exception?.printStackTrace()
                    toast(R.string.unkown_error)
                }
            }
        })
    }

    override fun switchVisibility() {
        return
    }

    override fun reInitViews() {
        viewModel.reInitFields()
    }


}