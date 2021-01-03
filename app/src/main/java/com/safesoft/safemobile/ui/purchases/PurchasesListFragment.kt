package com.safesoft.safemobile.ui.purchases

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentPurchasesListBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.BaseScannerFragment
import com.safesoft.safemobile.viewmodel.PurchasesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PurchasesListFragment : BaseScannerFragment() {

    private val galleryViewModel: PurchasesViewModel by viewModels(this::requireParentFragment)
    private lateinit var binding: FragmentPurchasesListBinding

    @Inject
    lateinit var recyclerAdapter: PurchasesRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_purchases_list, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = this
        binding.purchasesList.adapter = recyclerAdapter
        binding.purchasesSearchField.requestFocus()
        binding.purchasesSearchField.setOnLongClickListener {
            launchScanner()
            true
        }
    }

    override fun handleScannerResult(text: String) {
        super.handleScannerResult(text)
        binding.purchasesSearchField.setText(text)
        Log.d(TAG, "handleScannerResult: inserted $text into the field")
    }

    override fun setUpObservers() {
        super.setUpObservers()
        galleryViewModel.purchasesList.observe(
            viewLifecycleOwner,
            {
                recyclerAdapter.submitList(it)
                recyclerAdapter.notifyDataSetChanged()
            }
        )

        binding.purchasesSearchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString()
                if (query.isBlank()) {
                    if (!galleryViewModel.purchasesList.hasActiveObservers())
                        galleryViewModel.purchasesList.observe(
                            viewLifecycleOwner,
                            {
                                recyclerAdapter.submitList(it)
                                recyclerAdapter.notifyDataSetChanged()
                            }
                        )
                } else {
                    if (galleryViewModel.purchasesList.hasActiveObservers())
                        galleryViewModel.purchasesList.removeObservers(viewLifecycleOwner)

                    galleryViewModel.search(query).observe(
                        viewLifecycleOwner,
                        {
                            recyclerAdapter.submitList(it)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    )
                }
            }
        })

    }
}