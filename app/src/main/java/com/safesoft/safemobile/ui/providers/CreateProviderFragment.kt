package com.safesoft.safemobile.ui.providers

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.Clients
import com.safesoft.safemobile.backend.utils.doubleValue
import com.safesoft.safemobile.databinding.DialogFiscalInformationsBinding
import com.safesoft.safemobile.databinding.DialogNoteBinding
import com.safesoft.safemobile.databinding.FragmentCreateProviderBinding
import com.safesoft.safemobile.ui.generics.BaseFormOwner
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.GenericSpinnerAdapter
import com.safesoft.safemobile.viewmodel.ClientsViewModel
import com.safesoft.safemobile.viewmodel.ProvidersViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder


@AndroidEntryPoint
class CreateProviderFragment : BaseFragment(), BaseFormOwner {

    private val providersViewModel: ProvidersViewModel by viewModels(this::requireActivity)
    private val clientsViewModel: ClientsViewModel by viewModels(this::requireActivity)

    private lateinit var binding: FragmentCreateProviderBinding

    private val providerCodeDoubleTapEvent =
        GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean = providersViewModel.codeDoubleTap()
        })
    private val providerPhoneDoubleTapEvent =
        GestureDetector(context, object : SimpleOnGestureListener() {
            @SuppressLint("ClickableViewAccessibility")
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d(TAG, "Phone double tapped!" + binding.phones.childCount)
                if (!binding.phone.text.isNullOrBlank()) {
                    val view = layoutInflater.inflate(R.layout.phone_view, null)
                    binding.phones.addView(view)
                }
                return true
            }
        })
    private val providerFaxDoubleTapEvent =
        GestureDetector(context, object : SimpleOnGestureListener() {
            @SuppressLint("ClickableViewAccessibility")
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d(TAG, "Phone double tapped!" + binding.faxes.childCount)
                if (!binding.fax.text.isNullOrBlank()) {
                    val view = layoutInflater.inflate(R.layout.fax_view, null)
                    binding.faxes.addView(view)
                }
                return true
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_provider, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = providersViewModel
        binding.createProviderCode.setOnTouchListener { _, me ->
            providerCodeDoubleTapEvent.onTouchEvent(me)
        }
        binding.fiscalInfosButton.setOnClickListener {
            showFiscalDialog()
        }
        binding.extraNotesButton.setOnClickListener { showNoteDialog() }
        binding.phone.setOnTouchListener { _, me -> providerPhoneDoubleTapEvent.onTouchEvent(me) }
        binding.fax.setOnTouchListener { _, me -> providerFaxDoubleTapEvent.onTouchEvent(me) }
        binding.saveProvider.setOnClickListener { saveProvider() }

        val testItems = mutableListOf<Clients>()
        val adapter =
            GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, testItems)
        adapter.setNotifyOnChange(true)
        binding.createProviderClient.setOnItemClickListener { _, _, i, _ ->
            Log.e(TAG, "Item Clicked At: $i")
            providersViewModel.providerForm.fields.client.value = adapter.getItem(i)?.id
        }
        binding.createProviderClient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString()
                clientsViewModel.searchFlow(query).observe(viewLifecycleOwner, Observer {
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
        })
        binding.createProviderClient.setAdapter(adapter)

    }

    private fun showFiscalDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: DialogFiscalInformationsBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.dialog_fiscal_informations, null, false)
        fBinding.viewModel = providersViewModel
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        fBinding.closeFiscalProvider.setOnClickListener { dialog.dismiss() }
    }

    private fun showNoteDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: DialogNoteBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.dialog_note, null, false)
        fBinding.viewModel = providersViewModel
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        fBinding.closeFiscalProvider.setOnClickListener { dialog.dismiss() }
    }

    private fun checkMultiPhones(): String {
        val phones: StringBuilder = StringBuilder()
        if (!binding.phone.text.isNullOrBlank()) {
            phones.append(binding.phone.text)
            if (binding.phones.childCount > 1) {
                for (i in 1 until binding.phones.childCount)
                    phones.append(" ; ")
                        .append((binding.phones.getChildAt(i) as TextInputEditText).text)
            }
        }
        return phones.toString()
    }

    private fun checkMultiFaxes(): String {
        val faxes: StringBuilder = StringBuilder()
        if (!binding.fax.text.isNullOrBlank()) {
            faxes.append(binding.fax.text)
            if (binding.faxes.childCount > 1) {
                for (i in 1 until binding.faxes.childCount)
                    faxes.append(" ; ")
                        .append((binding.faxes.getChildAt(i) as TextInputEditText).text)
            }
        }
        return faxes.toString()
    }

    private fun saveProvider() {
        providersViewModel.providerForm.fields.phones.value = checkMultiPhones()
        providersViewModel.providerForm.fields.faxes.value = checkMultiFaxes()
        providersViewModel.providerForm.fields.initialSold.value =
            binding.createProviderInitialSold.text.toString().doubleValue()
        providersViewModel.saveProvider().observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> switchVisibility()
                success -> {
                    switchVisibility()
                    reInitViews()
                }
                error -> {
                    switchVisibility()
                    toast(R.string.unkown_error)
                }
            }
        })
    }

    override fun switchVisibility() {
        binding.saveProvider.visibility =
            if (binding.saveProvider.isVisible) View.GONE else View.VISIBLE
        binding.loading.visibility =
            if (binding.saveProvider.isVisible) View.GONE else View.VISIBLE
    }

    override fun reInitViews() {
        providersViewModel.reInitFields()
        binding.createProviderCity.setText("")
        binding.createProviderClient.setText("")
        binding.phone.setText("")
        binding.fax.setText("")
        binding.createProviderInitialSold.setText("")
        val childCount = binding.phones.childCount
        if (childCount > 1)
            for (i in 1 until childCount)
                binding.phones.removeViewAt(i)

        val childCount2 = binding.faxes.childCount
        if (childCount2 > 1)
            for (i in 1 until childCount2)
                binding.faxes.removeViewAt(i)
    }

}