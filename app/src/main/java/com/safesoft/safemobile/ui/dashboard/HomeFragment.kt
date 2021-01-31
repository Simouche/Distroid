package com.safesoft.safemobile.ui.dashboard

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentHomeBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.viewmodel.AuthViewModel
import com.safesoft.safemobile.viewmodel.ClientsViewModel
import com.safesoft.safemobile.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels(this::requireActivity)
    private val clientsViewModel: ClientsViewModel by viewModels(this::requireActivity)
    private val authViewModel: AuthViewModel by viewModels(this::requireActivity)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpViews()
    }

    override fun setUpViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = homeViewModel

        if (authViewModel.hasPurchaseModule() && authViewModel.isBuyer()) {
            binding.purchasesCard.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_nav_dashboard_to_nav_purchases, null
                )
            )
            binding.providersCard.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_nav_dashboard_to_nav_providers,
                    null
                )
            )
        } else {
            binding.purchasesCard.isEnabled = false
            binding.purchasesCard.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.providersCard.isEnabled = false
            binding.providersCard.setCardBackgroundColor(resources.getColor(R.color.gray))
        }

        if (authViewModel.hasSalesModule() && authViewModel.isSeller()) {
            binding.salesCard.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_nav_dashboard_to_nav_sales,
                    null
                )
            )
            binding.clientsCard.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_nav_dashboard_to_nav_clients,
                    null
                )
            )
        } else {
            binding.salesCard.isEnabled = false
            binding.salesCard.setCardBackgroundColor(resources.getColor(R.color.gray))
            binding.clientsCard.isEnabled = false
            binding.clientsCard.setCardBackgroundColor(resources.getColor(R.color.gray))
        }

        if (authViewModel.hasInventoryModule() && authViewModel.isInventorier()) {
            binding.inventoryCard.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_nav_dashboard_to_nav_inventory,
                    null
                )
            )
        } else {
            binding.inventoryCard.isEnabled = false
            binding.inventoryCard.setCardBackgroundColor(resources.getColor(R.color.gray))
        }

        binding.productsCard.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_nav_dashboard_to_nav_products,
                null
            )
        )
    }

    override fun setUpObservers() {
        super.setUpObservers()
        homeViewModel.getCount().observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> {
                    Log.d(TAG, "setUpObservers: " + it.data)
                    homeViewModel.counts.value = it.data
                }
                error -> errorHandler(it)
            }
        }
        )
    }

}