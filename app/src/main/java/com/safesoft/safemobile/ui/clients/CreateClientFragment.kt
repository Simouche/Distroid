package com.safesoft.safemobile.ui.clients

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.Providers
import com.safesoft.safemobile.databinding.DialogFiscalInformationsClientBinding
import com.safesoft.safemobile.databinding.DialogNoteClientBinding
import com.safesoft.safemobile.databinding.FragmentCreateClientBinding
import com.safesoft.safemobile.ui.generics.BaseFormOwner
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.ui.generics.adapter.GenericSpinnerAdapter
import com.safesoft.safemobile.viewmodel.ClientsViewModel
import com.safesoft.safemobile.viewmodel.ProvidersViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateClientFragment() : BaseFragment(), BaseFormOwner {
    val TAG = CreateClientFragment::class.simpleName

    private val viewModel: ClientsViewModel by viewModels(this::requireActivity)
    private val providersViewModel: ProvidersViewModel by viewModels(this::requireActivity)

    private lateinit var binding: FragmentCreateClientBinding

    private val clientCodeDoubleTapEvent =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean = viewModel.codeDoubleTap()
        })

    private val clientPhoneDoubleTapEvent =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
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
    private val clientFaxDoubleTapEvent =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_client, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.createClientCode.setOnTouchListener { _, me ->
            clientCodeDoubleTapEvent.onTouchEvent(me)
        }
        binding.fiscalInfosButton.setOnClickListener {
            showFiscalDialog()
        }
        binding.extraNotesButton.setOnClickListener { showNoteDialog() }
        binding.phone.setOnTouchListener { _, me -> clientPhoneDoubleTapEvent.onTouchEvent(me) }
        binding.fax.setOnTouchListener { _, me -> clientFaxDoubleTapEvent.onTouchEvent(me) }
        binding.saveClient.setOnClickListener { saveClient() }

        val testItems = mutableListOf<Providers>()
        val adapter =
            GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, testItems)
        adapter.setNotifyOnChange(true)
        binding.createClientProvider.setOnItemClickListener { _, _, i, _ ->
            Log.e(TAG, "Item Clicked At: $i")
            viewModel.clientForm.fields.provider.value = adapter.getItem(i)?.id
        }
        binding.createClientProvider.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString()
                providersViewModel.searchFlow(query).observe(viewLifecycleOwner, Observer {
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
        binding.createClientProvider.setAdapter(adapter)

    }

    private fun showFiscalDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: DialogFiscalInformationsClientBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(context),
                R.layout.dialog_fiscal_informations_client,
                null,
                false
            )
        fBinding.viewModel = viewModel
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        fBinding.closeFiscalProvider.setOnClickListener { dialog.dismiss() }
    }

    private fun showNoteDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: DialogNoteClientBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.dialog_note_client, null, false)
        fBinding.viewModel = viewModel
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

    private fun saveClient() {
        viewModel.clientForm.fields.phones.value = checkMultiPhones()
        viewModel.clientForm.fields.faxes.value = checkMultiFaxes()
        viewModel.clientForm.fields.initialSold.value =
            (binding.createClientInitialSold.text.toString() ?: "0.0").toDouble()
        viewModel.saveClient().observe(viewLifecycleOwner, Observer {
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
        binding.saveClient.visibility =
            if (binding.saveClient.isVisible) View.GONE else View.VISIBLE
        binding.loading.visibility =
            if (binding.saveClient.isVisible) View.GONE else View.VISIBLE
    }

    override fun reInitViews() {
        viewModel.reInitFields()
        binding.createClientCity.setText("")
        binding.createClientProvider.setText("")
        binding.phone.setText("")
        binding.fax.setText("")
        binding.createClientInitialSold.setText("")
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