package com.safesoft.safemobile.ui.clients

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentClientsListBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.BaseScannerFragment
import com.safesoft.safemobile.ui.generics.addDivider
import com.safesoft.safemobile.ui.generics.listeners.OnItemClickListener
import com.safesoft.safemobile.ui.generics.listeners.OnItemLongClickListener
import com.safesoft.safemobile.viewmodel.ClientsViewModel
import com.safesoft.safemobile.viewmodel.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClientsListFragment : BaseScannerFragment() {

    private val clientsViewModel: ClientsViewModel by viewModels(this::requireActivity)
    private val salesViewModel: SalesViewModel by viewModels(this::requireActivity)

    private lateinit var binding: FragmentClientsListBinding

    @Inject
    lateinit var recyclerAdapter: ClientsRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_clients_list, container, false)
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = clientsViewModel
        binding.clientsSearchField.requestFocus()
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
        binding.clientsList.adapter = recyclerAdapter
        binding.clientsList.addDivider()
        registerForContextMenu(binding.clientsList)
        binding.clientsSearchField.setOnLongClickListener {
            launchScanner()
            true
        }
    }

    override fun handleScannerResult(text: String) {
        super.handleScannerResult(text)
        binding.clientsSearchField.setText(text)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        if (!clientsViewModel.clientsList.hasActiveObservers() && !clientsViewModel.searchQuery.hasActiveObservers())
            clientsViewModel.clientsList.observe(
                viewLifecycleOwner,
                Observer {
                    recyclerAdapter.submitList(it)
                    recyclerAdapter.notifyDataSetChanged()
                }
            )

        if (!clientsViewModel.searchQuery.hasActiveObservers())
            clientsViewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
                if (query.isBlank()) {
                    if (!clientsViewModel.clientsList.hasActiveObservers())
                        clientsViewModel.clientsList.observe(
                            viewLifecycleOwner,
                            Observer {
                                recyclerAdapter.submitList(it)
                                recyclerAdapter.notifyDataSetChanged()
                            }
                        )
                } else {
                    if (clientsViewModel.clientsList.hasActiveObservers()) clientsViewModel.clientsList.removeObservers(
                        viewLifecycleOwner
                    )
                    clientsViewModel.search(query).observe(
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
                salesViewModel.client = recyclerAdapter.getItemAt(currentItemPosition)!!.id
                salesViewModel.salesForm.fields.client.value =
                    recyclerAdapter.getItemAt(currentItemPosition)!!.id
                salesViewModel.clientName =
                    recyclerAdapter.getItemAt(currentItemPosition).toString()
                findNavController().navigate(R.id.action_nav_clients_to_nav_create_sale)

            }
        }
        return true
    }

}