package com.safesoft.safemobile.ui.providers

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentProvidersListBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.BaseScannerFragment
import com.safesoft.safemobile.ui.generics.addDivider
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.listeners.OnItemLongClickListener
import com.safesoft.safemobile.viewmodel.ProvidersViewModel
import com.safesoft.safemobile.viewmodel.PurchasesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProvidersListFragment : BaseScannerFragment() {

    private val providersViewModel: ProvidersViewModel by viewModels(this::requireActivity)
    private val purchasesViewModel: PurchasesViewModel by viewModels(this::requireActivity)

    private lateinit var binding: FragmentProvidersListBinding

    @Inject
    lateinit var recyclerAdapter: ProvidersRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_providers_list, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = providersViewModel
        binding.providersSearchField.requestFocus()
        recyclerAdapter.onNormalClickListener =
            OnItemClickListener { position, _ ->
                toast(null, "$position")
            }
        recyclerAdapter.onLongClickListener =
            OnItemLongClickListener { position, view ->
                currentItemPosition = position
                view.showContextMenu()
                true
            }
        binding.providersList.adapter = recyclerAdapter
        binding.providersList.addDivider()
        registerForContextMenu(binding.providersList)
        binding.providersSearchField.setOnLongClickListener {
            launchScanner()
            true
        }
    }

    override fun handleScannerResult(text: String) {
        super.handleScannerResult(text)
        binding.providersSearchField.setText(text)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        if (!providersViewModel.providersList.hasActiveObservers() && (!providersViewModel.searchQuery.hasActiveObservers()))
            providersViewModel.providersList.observe(
                viewLifecycleOwner,
                Observer {
                    recyclerAdapter.submitList(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            )

        if (!providersViewModel.searchQuery.hasActiveObservers())
            providersViewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
                if (query.isBlank()) {
                    if (!providersViewModel.providersList.hasActiveObservers())
                        providersViewModel.providersList.observe(
                            viewLifecycleOwner,
                            Observer {
                                recyclerAdapter.submitList(it)
                                recyclerAdapter.notifyDataSetChanged()
                            }
                        )
                } else {
                    if (providersViewModel.providersList.hasActiveObservers()) providersViewModel.providersList.removeObservers(
                        viewLifecycleOwner
                    )
                    providersViewModel.search(query).observe(
                        viewLifecycleOwner,
                        Observer {
                            recyclerAdapter.submitList(it)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    )
                }
            }
            )
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.provider_client_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update -> toast(null, "Update clicked on $currentItemPosition")
            R.id.action_details -> toast(null, "details clicked on $currentItemPosition")
            R.id.action_new_receipt -> {
                purchasesViewModel.provider = recyclerAdapter.getItemAt(currentItemPosition)!!.id
                purchasesViewModel.purchaseForm.fields.provider.value =
                    recyclerAdapter.getItemAt(currentItemPosition)!!.id
                purchasesViewModel.providerName =
                    recyclerAdapter.getItemAt(currentItemPosition).toString()
                findNavController().navigate(R.id.action_nav_providers_to_nav_create_purchase)

            }
        }
        return true
    }
}