package com.safesoft.safemobile.ui.products

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.entity.Barcodes
import com.safesoft.safemobile.backend.db.entity.Brands
import com.safesoft.safemobile.backend.utils.doubleValue
import com.safesoft.safemobile.backend.utils.formatted
import com.safesoft.safemobile.backend.utils.isEmptyOrBlank
import com.safesoft.safemobile.databinding.DialogCreateBrandBinding
import com.safesoft.safemobile.databinding.FragmentCreateProductBinding
import com.safesoft.safemobile.ui.generics.*
import com.safesoft.safemobile.ui.generics.adapter.GenericSpinnerAdapter
import com.safesoft.safemobile.viewmodel.ProductsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.File
import kotlinx.android.synthetic.main.fragment_create_product.*


@AndroidEntryPoint
class CreateProductFragment : BaseFragment(), ProductCalculator, BaseFormOwner {

    private val TAG = CreateProductFragment::class.simpleName
    private val SCAN_BARCODE = 100

    private val viewModel: ProductsViewModel by viewModels(this::requireActivity)

    private lateinit var binding: FragmentCreateProductBinding
    private lateinit var easyImage: EasyImage

    private val barcodeDoubleTapEvent =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d(TAG, "onDoubleTap: barcode double tapped!")
                if (binding.productBarcode.text!!.isEmptyOrBlank())
                    binding.productBarcode.setText(viewModel.barcodeDoubleTap())
                else {
                    addChild()
                }
                return true
            }
        })

    private val brandDoubleTapEvent =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                showCreateBrandDialog()
                return true
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_product, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.productBarcode.setOnTouchListener { _, me -> barcodeDoubleTapEvent.onTouchEvent(me) }
        binding.productBarcode.setOnLongClickListener {
            startActivityForResult(
                Intent(requireContext(), ScannerActivity::class.java),
                SCAN_BARCODE
            )
            true
        }
        binding.createProductBrand.setOnTouchListener { _, me -> brandDoubleTapEvent.onTouchEvent(me) }

        val initItems = mutableListOf<Brands>()
        val adapter = GenericSpinnerAdapter(requireContext(), R.layout.spinner_item, initItems)
        adapter.setNotifyOnChange(true)
        binding.createProductBrand.setOnItemClickListener { _, _, i, _ ->
            Log.e(TAG, "Item Clicked At: $i")
            viewModel.productsForm.fields.brand.value = adapter.getItem(i)?.id
        }
        binding.createProductBrand.onTextChanged { query ->
            Log.d(TAG, "setUpViews: the query is $query")
            viewModel.searchBrandFlow(query).observe(viewLifecycleOwner, Observer {
                when (it.state) {
                    loading, error -> return@Observer
                    success -> {
                        Log.d(TAG, "setUpViews: success data=${it.data?.size}")
                        adapter.objects = it.data!!
                        adapter.notifyDataSetChanged()
                    }
                    error -> return@Observer
                }
            })
        }
        binding.createProductBrand.setAdapter(adapter)

        binding.createProductImage.setOnClickListener { selectImage() }

        if (viewModel.productsForm.fields.photo.value != null) {
            Picasso
                .get()
                .load(File(viewModel.productsForm.fields.photo.value!!))
                .placeholder(R.drawable.ic_product)
                .error(R.drawable.ic_product)
                .fit()
                .into(binding.createProductImage)
            Log.d(TAG, "setUpViews: ${viewModel.productsForm.fields.photo.value}")
        }

        binding.createProductSaveButton.setOnClickListener { saveProduct() }
        binding.discountSwitch.isChecked = viewModel.isDiscount.value ?: false
    }

    private fun saveProduct() {
        viewModel.saveProduct().observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> {
                    viewModel.saveBarCodes(checkMultiBarCodes())
                    reInitViews()
                }
                error -> {
                    toast(R.string.unkown_error)
                    it.exception?.printStackTrace()
                }
            }
        })
    }

    private fun checkMultiBarCodes(): List<Barcodes> {
        val barCodes = mutableListOf<Barcodes>()
        for (i in 0 until binding.createProductBarcodes.childCount)
            barCodes.add(
                Barcodes(
                    0,
                    (binding.createProductBarcodes.getChildAt(i) as TextInputLayout).editText?.text.toString(),
                    0
                )
            )
        return barCodes
    }

    private fun selectImage() {
        easyImage = EasyImage.Builder(requireContext())
            .setChooserTitle(getString(R.string.select_product_image))
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .build()
        easyImage.openChooser(this)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        binding.discountSwitch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            viewModel.isDiscount.value =
                b
        }
        viewModel.isDiscount.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.discountAmountLayout.visibility = View.VISIBLE
            else
                binding.discountAmountLayout.visibility = View.INVISIBLE
        })
        pricesObservers()
    }

    private fun showCreateBrandDialog() {
        Log.d(TAG, "Show Dialog Clicked!")
        val fBinding: DialogCreateBrandBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.dialog_create_brand, null, false)
        fBinding.viewModel = viewModel
        fBinding.lifecycleOwner = viewLifecycleOwner
        val dialog =
            AlertDialog.Builder(requireContext()).setView(fBinding.root).setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        fBinding.createBrandSaveButton.setOnClickListener {
            saveBrandObserver(dialog)
        }
        fBinding.createBrandSaveAndContinueButton.setOnClickListener {
            saveBrandObserver(dialog, false)
        }
        fBinding.createBrandCloseButton.setOnClickListener { dialog.dismiss() }
    }

    private fun saveBrandObserver(dialog: AlertDialog, dismiss: Boolean = true) {
        viewModel.saveBrand().observe(viewLifecycleOwner, Observer {
            when (it.state) {
                loading -> return@Observer
                success -> {
                    if (dismiss) dialog.dismiss()
                    Log.d(TAG, "Added with success ${it.data}")
                }
                error -> errorHandler(it)
            }
        }
        )
    }

    /**
     * calculations details will be here
     *
     * */
    private fun pricesObservers() {

        viewModel.getFields().purchasePriceHT.observe(viewLifecycleOwner, Observer {
            binding.createProductPurchasePriceTtc.setText(
                calculateNewPrice(
                    it.doubleValue(),
                    binding.createProductTva.doubleValue()
                ).formatted()
            )
        })
        viewModel.getFields().tva.observe(viewLifecycleOwner, Observer {
            if (binding.createProductTva.isFocused) {
                binding.createProductPurchasePriceTtc.setText(
                    calculateNewPrice(
                        binding.createProductPurchasePriceHt.doubleValue(),
                        it.doubleValue()
                    ).formatted()
                )
            }
        })
        viewModel.getFields().purchasePriceTTC.observe(viewLifecycleOwner, Observer {
            if (binding.createProductPurchasePriceTtc.isFocused) {
                binding.createProductTva.setText(
                    calculatePercentageDiff(
                        binding.createProductPurchasePriceHt.doubleValue(),
                        it.doubleValue()
                    ).formatted()
                )
            }
        })

        viewModel.getFields().marge.observe(viewLifecycleOwner, Observer {
            if (binding.createProductMargeDetail.isFocused) {
                binding.createProductSellPriceDetailHt.setText(
                    calculateNewPrice(
                        viewModel.getFields().purchasePriceHT.value!!.doubleValue(),
                        it.doubleValue()
                    ).formatted()
                )
                binding.createProductSellPriceDetailTtc.setText(
                    calculateNewPrice(
                        viewModel.getFields().sellPriceDetailHT.value!!.doubleValue(),
                        viewModel.getFields().tva.value!!.doubleValue()
                    ).formatted()
                )
            }
        })
        viewModel.getFields().sellPriceDetailHT.observe(viewLifecycleOwner, Observer {
            if (binding.createProductSellPriceDetailHt.isFocused) {
/*
may reactivate this in case of the user wants to change the marge when changing the sell price HT
                binding.createProductMargeDetail.setText(
                    calculatePercentageDiff(
                        it.doubleValue(),
                        viewModel.getFields().sellPriceDetailTTC.value!!.doubleValue()
                    ).toString()
                )*/
                binding.createProductSellPriceDetailTtc.setText(
                    calculateNewPrice(
                        it.doubleValue(),
                        viewModel.getFields().tva.value!!.doubleValue()
                    ).formatted()
                )
            }
        })

        viewModel.getFields().margeWhole.observe(viewLifecycleOwner, Observer {
            if (binding.createProductMargeWhole.isFocused) {
                binding.createProductSellPriceWholeHt.setText(
                    calculateNewPrice(
                        viewModel.getFields().purchasePriceTTC.value!!.doubleValue(),
                        it.doubleValue()
                    ).formatted()
                )
                binding.createProductSellPriceWholeTtc.setText(
                    calculateNewPrice(
                        viewModel.getFields().sellPriceWholeHT.value!!.doubleValue(),
                        viewModel.getFields().tva.value!!.doubleValue()
                    ).formatted()
                )
            }
        })
        viewModel.getFields().sellPriceDetailHT.observe(viewLifecycleOwner, Observer {
            if (binding.createProductSellPriceWholeHt.isFocused) {
/*
may reactivate this in case of the user wants to change the marge when changing the sell price HT
                binding.createProductMargeDetail.setText(
                    calculatePercentageDiff(
                        it.doubleValue(),
                        viewModel.getFields().sellPriceDetailTTC.value!!.doubleValue()
                    ).toString()
                )*/
                binding.createProductSellPriceWholeTtc.setText(
                    calculateNewPrice(
                        it.doubleValue(),
                        viewModel.getFields().tva.value!!.doubleValue()
                    ).formatted()
                )
            }
        })

        viewModel.getFields().margeHalfWhole.observe(viewLifecycleOwner, Observer {
            if (binding.createProductMargeHalfWhole.isFocused) {
                binding.createProductSellPriceHalfWholeHt.setText(
                    calculateNewPrice(
                        viewModel.getFields().purchasePriceTTC.value!!.doubleValue(),
                        it.doubleValue()
                    ).formatted()
                )
                binding.createProductSellPriceHalfWholeTtc.setText(
                    calculateNewPrice(
                        viewModel.getFields().sellPrice3HT.value!!.doubleValue(),
                        viewModel.getFields().tva.value!!.doubleValue()
                    ).formatted()
                )
            }
        })
        viewModel.getFields().sellPrice3HT.observe(viewLifecycleOwner, Observer {
            if (binding.createProductSellPriceHalfWholeHt.isFocused) {
/*
may reactivate this in case of the user wants to change the marge when changing the sell price HT
                binding.createProductMargeDetail.setText(
                    calculatePercentageDiff(
                        it.doubleValue(),
                        viewModel.getFields().sellPriceDetailTTC.value!!.doubleValue()
                    ).toString()
                )*/
                binding.createProductSellPriceHalfWholeTtc.setText(
                    calculateNewPrice(
                        it.doubleValue(),
                        viewModel.getFields().tva.value!!.doubleValue()
                    ).formatted()
                )
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (!data?.getStringExtra("code").isNullOrEmpty()) {
                    handleBarcodeScan(data?.getStringExtra("code")!!)
                }
            } else {
                toast(R.string.nothing_scanned)
            }
        } else {
            easyImage.handleActivityResult(
                requestCode,
                resultCode,
                data,
                requireActivity(),
                object : EasyImage.Callbacks {
                    override fun onCanceled(source: MediaSource) {
                        toast(R.string.no_image_picked)
                    }

                    override fun onImagePickerError(error: Throwable, source: MediaSource) {
                        toast(R.string.unkown_error)
                    }

                    override fun onMediaFilesPicked(
                        imageFiles: Array<MediaFile>,
                        source: MediaSource
                    ) {
                        viewModel.productsForm.fields.photo.value =
                            imageFiles[0].file.absolutePath
                    }
                })
        }
    }

    override fun switchVisibility() {
        binding.createProductSaveButton.visibility =
            if (binding.createProductSaveButton.isVisible) View.GONE else View.VISIBLE
        binding.loading.visibility =
            if (binding.createProductSaveButton.isVisible) View.GONE else View.VISIBLE
    }

    override fun reInitViews() {
        viewModel.reInitFields()
        binding.createProductImage.setImageResource(R.drawable.ic_product)

        val childCount = binding.createProductBarcodes.childCount
        if (childCount > 1)
            for (i in 1 until childCount)
                binding.createProductBarcodes.removeViewAt(i)
    }

    private fun handleBarcodeScan(code: String) {
        if (binding.productBarcode.text.toString().isEmptyOrBlank()) {
            binding.productBarcode.setText(code)
            viewModel.codesList.value?.set(0, code)
        } else {
            val childCount = binding.createProductBarcodes.childCount
            for (i in 1 until childCount) {
                val child =
                    (binding.createProductBarcodes.getChildAt(i) as TextInputLayout).editText
                if (child != null && child.text.toString().isEmptyOrBlank()) {
                    child.setText(code)
                    if (i > viewModel.codesList.value?.size!! - 1)
                        viewModel.codesList.value?.add(code)
                    else
                        viewModel.codesList.value?.set(i - 1, code)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        restoreChildren()
    }

    override fun onPause() {
        super.onPause()
        saveChildrenState()
        Log.d(TAG, "onPause: Paused!")
    }

    private fun restoreChildren() {
        if (binding.createProductBarcodes.childCount - 1 < viewModel.codesList.value!!.size)
            for (i in 0 until viewModel.codesList.value!!.size)
                addChild(i)
    }

    private fun saveChildrenState() {
        binding.createProductBarcodes.children.forEachIndexed { index, view ->
            if (index != 0) {
                viewModel.codesList.value?.set(
                    index - 1,
                    (view as TextInputLayout).editText?.text.toString()
                )
                Log.d(
                    TAG,
                    "saveChildrenState: added a barcode ${viewModel.codesList.value?.get(index - 1)}"
                )
            }

        }
    }

    private fun addChild(index: Int = -1) {
        val view = layoutInflater.inflate(R.layout.barcode_view, null)
        if (index != -1)
            (view as TextInputLayout).editText?.setText(viewModel.codesList.value?.get(index))
        else
            viewModel.codesList.value?.add("temporary")
        binding.createProductBarcodes.addView(view)
    }

}