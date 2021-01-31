package com.safesoft.safemobile.ui.sales

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentSalesListBinding
import com.safesoft.safemobile.ui.generics.BaseScannerFragment
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.listeners.OnItemLongClickListener
import com.safesoft.safemobile.viewmodel.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SalesListFragment : BaseScannerFragment() {

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
        recyclerAdapter.onNormalClickListener =
            OnItemClickListener { position, _ ->
                toast(null, "$position")
            }
        recyclerAdapter.onLongClickListener = OnItemLongClickListener { position, view ->
            currentItemPosition = position
            view.showContextMenu()
        }
        registerForContextMenu(binding.salesList)

        binding.salesSearchField.requestFocus()
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.salesList.observe(
            viewLifecycleOwner,
            {
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

        binding.salesSearchField.setOnLongClickListener {
            launchScanner()
            true
        }
    }

    override fun handleScannerResult(text: String) {
        super.handleScannerResult(text)
        binding.salesSearchField.setText(text)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.receipt_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.standard_action_update -> toast(null, "Update clicked on $currentItemPosition")
            R.id.action_print -> {
                printSale(recyclerAdapter.getItemAt(currentItemPosition)!!.id)
                success(R.string.printing)
            }
        }
        return true
    }

}