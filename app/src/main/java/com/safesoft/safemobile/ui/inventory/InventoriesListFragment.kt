package com.safesoft.safemobile.ui.inventory

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentInventoriesListBinding
import com.safesoft.safemobile.databinding.FragmentInventoryListItemBinding
import com.safesoft.safemobile.databinding.FragmentProvidersListBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.addDivider
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.listeners.OnItemLongClickListener
import com.safesoft.safemobile.ui.providers.ProvidersListFragment
import com.safesoft.safemobile.ui.providers.ProvidersRecyclerAdapter
import com.safesoft.safemobile.viewmodel.InventoryViewModel
import com.safesoft.safemobile.viewmodel.ProvidersViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InventoriesListFragment : BaseFragment() {
    private val TAG = InventoriesListFragment::class.simpleName

    private val inventoryViewModel: InventoryViewModel by viewModels(this::requireActivity)
    private lateinit var binding: FragmentInventoriesListBinding

    @Inject
    lateinit var recyclerAdapter: InventoriesRecyclerAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_inventories_list, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = inventoryViewModel
        binding.inventorySearchField.requestFocus()
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
        binding.inventoriesList.adapter = recyclerAdapter
        binding.inventoriesList.addDivider()
        registerForContextMenu(binding.inventoriesList)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        if (!inventoryViewModel.inventoriesList.hasActiveObservers() && (!inventoryViewModel.searchQuery.hasActiveObservers()))
            inventoryViewModel.inventoriesList.observe(
                viewLifecycleOwner,
                Observer {
                    recyclerAdapter.submitList(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            )

        if (!inventoryViewModel.searchQuery.hasActiveObservers())
            inventoryViewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
                if (query.isBlank()) {
                    if (!inventoryViewModel.inventoriesList.hasActiveObservers())
                        inventoryViewModel.inventoriesList.observe(
                            viewLifecycleOwner,
                            Observer {
                                recyclerAdapter.submitList(it)
                                recyclerAdapter.notifyDataSetChanged()
                            }
                        )
                } else {
                    if (inventoryViewModel.inventoriesList.hasActiveObservers()) inventoryViewModel.inventoriesList.removeObservers(
                        viewLifecycleOwner
                    )
                    inventoryViewModel.search(query).observe(
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
        inflater.inflate(R.menu.standard_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update -> toast(null, "Update clicked on $currentItemPosition")
            R.id.action_details -> toast(null, "details clicked on $currentItemPosition")
            R.id.action_new_receipt -> toast(null, "new receipt clicked on $currentItemPosition")
        }
        return true
    }

}