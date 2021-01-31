package com.safesoft.safemobile.ui.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.safesoft.safemobile.MainActivity
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.utils.doubleValue
import com.safesoft.safemobile.backend.utils.formatted
import com.safesoft.safemobile.databinding.EnterpriseHeaderLayoutBinding
import com.safesoft.safemobile.databinding.FooterLayoutBinding
import com.safesoft.safemobile.databinding.FragmentApplicationSettingsBinding
import com.safesoft.safemobile.databinding.InvoiceConfirmationDialogBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.onTextChanged
import com.safesoft.safemobile.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplicationSettings : BaseFragment() {

    private val viewModel: SettingsViewModel by viewModels(this::requireActivity)
    private lateinit var binding: FragmentApplicationSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_application_settings,
            container,
            false
        )
        return binding.root
    }

    override fun setUp() {
        super.setUp()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.useFlash.observe(viewLifecycleOwner, viewModel::setUseFlash)
        viewModel.useAutoFocus.observe(viewLifecycleOwner, viewModel::setUseAutoFocus)
        viewModel.allowNegativeStock.observe(viewLifecycleOwner, viewModel::setAllowNegativeStock)
        viewModel.tvaValue.observe(viewLifecycleOwner, viewModel::setTvaValue)
        viewModel.showHeader.observe(viewLifecycleOwner, viewModel::setShowHeader)
        viewModel.showFooter.observe(viewLifecycleOwner, viewModel::setShowFooter)
//        viewModel.tarifactionMode.observe(viewLifecycleOwner, viewModel::setTarifactionMode)
        viewModel.selectedPrinter.observe(viewLifecycleOwner, viewModel::setSelectedPrinter)
        viewModel.enterpriseName.observe(viewLifecycleOwner, viewModel::setEnterpriseName)
        viewModel.enterpriseAddress.observe(viewLifecycleOwner, viewModel::setEnterpriseAddress)
        viewModel.enterprisePhone.observe(viewLifecycleOwner, viewModel::setEnterprisePhone)
        viewModel.enterpriseFooter.observe(viewLifecycleOwner, viewModel::setEnterpriseFooter)

        viewModel.showHeader.observe(viewLifecycleOwner, {
            if (it) {
                showHeaderDialog()
            }
        })

        viewModel.showFooter.observe(viewLifecycleOwner, {
            if (it) {
                showFooterDialog()
            }
        })

    }

    private fun showHeaderDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: EnterpriseHeaderLayoutBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(context),
                R.layout.enterprise_header_layout,
                null,
                false
            )
        fBinding.viewModel = viewModel
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.setOnCancelListener {
            viewModel.showHeader.value = false
        }
        dialog.show()
        fBinding.validate.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showFooterDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: FooterLayoutBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(context),
                R.layout.footer_layout,
                null,
                false
            )
        fBinding.viewModel = viewModel
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.setOnCancelListener {
            viewModel.showFooter.value = false
        }
        dialog.show()
        fBinding.validate.setOnClickListener {
            dialog.dismiss()
        }
    }

}