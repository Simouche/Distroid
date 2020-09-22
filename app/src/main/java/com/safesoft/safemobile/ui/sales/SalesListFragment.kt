package com.safesoft.safemobile.ui.sales

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentSalesListBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.viewmodel.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SalesListFragment : BaseFragment() {

    private val viewModel: SalesViewModel by viewModels(this::requireActivity)
    private lateinit var binding: FragmentSalesListBinding

    @Inject
    lateinit var recyclerAdapter: SalesRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sales_list, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = this
        binding.salesList.adapter = recyclerAdapter
        binding.salesSearchField.requestFocus()
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.salesList.observe(
            viewLifecycleOwner,
            Observer {
                recyclerAdapter.submitList(it)
                recyclerAdapter.notifyDataSetChanged()
            }
        )

        binding.salesSearchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString()
                if (query.isBlank()) {
                    if (!viewModel.salesList.hasActiveObservers())
                        viewModel.salesList.observe(
                            viewLifecycleOwner,
                            Observer {
                                recyclerAdapter.submitList(it)
                                recyclerAdapter.notifyDataSetChanged()
                            }
                        )
                } else {
                    if (viewModel.salesList.hasActiveObservers()) viewModel.salesList.removeObservers(
                        viewLifecycleOwner
                    )

                    viewModel.search(query).observe(
                        viewLifecycleOwner,
                        Observer {
                            recyclerAdapter.submitList(it)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    )
                }
            }
        })

    }

}