package com.safesoft.safemobile.viewmodel

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.db.local.entity.Barcodes
import com.safesoft.safemobile.backend.db.local.entity.Brands
import com.safesoft.safemobile.backend.db.local.entity.Products
import com.safesoft.safemobile.backend.repository.ProductsRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.backend.utils.ResourceState
import com.safesoft.safemobile.backend.utils.doubleValue
import com.safesoft.safemobile.forms.ProductsForm
import kotlinx.coroutines.launch

class ProductsViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository,
    private val config: PagedList.Config,
    val productsForm: ProductsForm
) : BaseViewModel(), BaseFormOwner {

    private val TAG = ProductsViewModel::class.simpleName

    var operation = 0

    var page = 0

    val codesList = MutableLiveData<MutableList<String>>().apply { value = mutableListOf() }

    val searchQuery = MutableLiveData<String>()

    val isDiscount = MutableLiveData<Boolean>().apply { value = false }

    val productsList: LiveData<PagedList<AllAboutAProduct>> =
        productsRepository.getAllProductsAllInfo().toLiveData(config = config)

    fun search(query: String): LiveData<PagedList<AllAboutAProduct>> =
        productsRepository.searchProduct(query).toLiveData(config = config)

    val onFocusBarCode: View.OnFocusChangeListener = View.OnFocusChangeListener { view, focused ->
        run {
            val et = view as EditText
            if (et.text.isNotEmpty() && !focused)
                productsForm.isBarcodeValid(true)
        }
    }

    val onFocusReference: View.OnFocusChangeListener = View.OnFocusChangeListener { view, focused ->
        run {
            val et = view as EditText
            if (et.text.isNotEmpty() && !focused)
                productsForm.isReferenceValid(true)
        }
    }

    val onFocusDesignation: View.OnFocusChangeListener =
        View.OnFocusChangeListener { view, focused ->
            run {
                val et = view as EditText
                if (et.text.isNotEmpty() && !focused)
                    productsForm.isDesignationValid(true)
            }
        }

    fun barcodeDoubleTap() = Products.generateBarCode()

    fun searchFlow(query: String): LiveData<Resource<List<AllAboutAProduct>>> {
        val data = MutableLiveData<Resource<List<AllAboutAProduct>>>()
        enqueue(productsRepository.searchProductFlow(query), data)
        return data
    }

    fun getFields() = productsForm.fields

    fun searchBrandFlow(query: String): LiveData<Resource<List<Brands>>> {
        val data = MutableLiveData<Resource<List<Brands>>>()
        enqueue(productsRepository.getAllBrands(query), data)
        return data
    }

    private fun saveProduct(vararg products: Products): LiveData<Resource<List<Long>>> {
        val data = MutableLiveData<Resource<List<Long>>>()
        enqueue(productsRepository.addProducts(*products), data)
        return data
    }

    fun saveProduct(): LiveData<Resource<List<Long>>> {
        val fields = productsForm.fields
        val product = Products(
            0,
            reference = fields.reference.value!!,
            designation = fields.designation.value!!,
            quantity = fields.quantity.value?.toDouble(),
            purchasePriceHT = fields.purchasePriceHT.value?.doubleValue(),
            tva = fields.tva.value?.doubleValue(),
            purchasePriceTTC = fields.purchasePriceTTC.value?.doubleValue(),
            marge = fields.marge.value?.doubleValue(),
            sellPriceDetailHT = fields.sellPriceDetailHT.value?.doubleValue() ?: 0.0,
            sellPriceDetailTTC = fields.sellPriceDetailTTC.value?.doubleValue() ?: 0.0,
            sellPriceWholeHT = fields.sellPriceWholeHT.value?.doubleValue() ?: 0.0,
            sellPriceWholeTTC = fields.sellPriceWholeTTC.value?.doubleValue() ?: 0.0,
            sellPriceHalfWholeHT = fields.sellPrice3HT.value?.doubleValue() ?: 0.0,
            sellPriceHalfWholeTTC = fields.sellPrice3TTC.value?.doubleValue() ?: 0.0,
            brand = fields.brand.value,
            promotion = fields.promotion.value?.toDouble() ?: 0.0,
            photo = fields.photo.value,
            synched = false,
            inApp = true
        )
        return saveProduct(product)
    }

    fun saveBarCodes(barCodes: List<Barcodes>) {
        viewModelScope.launch {
            val product = productsRepository.getLatestProduct()
            val newList = barCodes.map { it.copy(product = product.id) }.toTypedArray()
            try {
                productsRepository.addBarCodes(*newList)
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
            }
        }
    }

    override fun reInitFields() {
        val fields = productsForm.fields
        fields.barcode.value = ""
        fields.reference.value = ""
        fields.designation.value = ""
        fields.quantity.value = "0.0"
        fields.purchasePriceHT.value = "0.0"
        fields.purchasePriceTTC.value = "19.0"
        fields.steadyPurchasePriceHT.value = " 0.0"
        fields.steadyPurchasePriceTTC.value = "0.0"
        fields.marge.value = "0.0"
        fields.sellPriceDetailHT.value = "0.0"
        fields.sellPriceWholeHT.value = "0.0"
        fields.sellPrice3HT.value = "0.0"
        fields.sellPriceDetailTTC.value = " 0.0"
        fields.sellPriceWholeTTC.value = "0.0"
        fields.sellPrice3TTC.value = " 0.0"
        fields.photo.value = ""
        fields.quantityPerPackage.value = 0
        fields.promotion.value = " 0.0"
        fields.brand.value = 0
        fields.brandName.value = ""
        isDiscount.value = false
    }

    // TODO: 9/8/2020 discount on only one barcode with dates

    fun saveBrand(): MutableLiveData<Resource<Int>> {
        val data = MutableLiveData<Resource<Int>>()
        if (productsForm.isBrandNameValid()) {
            val brand = Brands(0, productsForm.fields.brandName.value!!)
            enqueue(productsRepository.addBrand(brand), data)
            productsForm.fields.brandName.value = " "
            Log.d(TAG, "saveBrand: Finished")
        } else {
            data.value =
                Resource<Int>(state = ResourceState.ERROR, messageId = R.string.brand_name_empty)
        }
        return data
    }

}