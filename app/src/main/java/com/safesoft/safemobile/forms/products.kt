package com.safesoft.safemobile.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.R
import com.safesoft.safemobile.BR
import com.safesoft.safemobile.backend.utils.isEmptyOrBlank
import javax.inject.Inject

class ProductsFields @Inject constructor() {
    var barcode = MutableLiveData<String>()
    var reference = MutableLiveData<String>()
    var designation = MutableLiveData<String>()
    var quantity = MutableLiveData<String>().apply { value = "0.0" }
    var purchasePriceHT = MutableLiveData<String>().apply { value = "0.0" }
    var tva = MutableLiveData<String>().apply { value = "19.0" }
    var purchasePriceTTC = MutableLiveData<String>().apply { value = " 0.0" }
    var steadyPurchasePriceHT = MutableLiveData<String>().apply { value = "0.0" }
    var steadyPurchasePriceTTC = MutableLiveData<String>().apply { value = "0.0" }
    var marge = MutableLiveData<String>().apply { value = "0.0" }
    var margeHalfWhole = MutableLiveData<String>().apply { value = "0.0" }
    var margeWhole = MutableLiveData<String>().apply { value = "0.0" }
    var sellPriceDetailHT = MutableLiveData<String>().apply { value = " 0.0" }
    var sellPriceWholeHT = MutableLiveData<String>().apply { value = "0.0" }
    var sellPrice3HT = MutableLiveData<String>().apply { value = " 0.0" }
    var sellPriceDetailTTC = MutableLiveData<String>().apply { value = " 0.0" }
    var sellPriceWholeTTC = MutableLiveData<String>().apply { value = " 0.0" }
    var sellPrice3TTC = MutableLiveData<String>().apply { value = "0.0" }
    var photo = MutableLiveData<String>()
    var quantityPerPackage = MutableLiveData<Long>()
    var promotion = MutableLiveData<String>().apply { value = "0.0" }
    var brand = MutableLiveData<Long>()
    var brandName = MutableLiveData<String>()
}

class ProductsErrorFields @Inject constructor() {
    var barcode: Int? = null
    var reference: Int? = null
    var designation: Int? = null
    var quantity: Int? = null
    var purchasePriceHT: Int? = null
    var tva: Int? = null
    var purchasePriceTTC: Int? = null
    var steadyPurchasePriceHT: Int? = null
    var steadyPurchasePriceTTC: Int? = null
    var marge: Int? = null
    var margeHalfWhole: Int? = null
    var margeWhole: Int? = null
    var sellPriceDetailHT: Int? = null
    var sellPriceWholeHT: Int? = null
    var sellPrice3HT: Int? = null
    var sellPriceDetailTTC: Int? = null
    var sellPriceWholeTTC: Int? = null
    var sellPrice3TTC: Int? = null
    var photo: Int? = null
    var quantityPerPackage: Int? = null
    var promotion: Int? = null
    var brand: Int? = null
}

class ProductsForm @Inject constructor(
    val fields: ProductsFields,
    private val errors: ProductsErrorFields
) : BaseObservable() {

    @Bindable
    fun isProductValid(): Boolean {
        return isBarcodeValid(false) && isDesignationValid(false) && isReferenceValid(false)
    }

    @Bindable
    fun getReferenceError(): Int? = errors.reference

    @Bindable
    fun getBarcodeError(): Int? = errors.barcode

    @Bindable
    fun getDesignationError(): Int? = errors.designation

    @Bindable
    fun getQuantityError(): Int? = errors.quantity

    @Bindable
    fun getPurchasePriceHTError(): Int? = errors.purchasePriceHT

    @Bindable
    fun getPurchasePriceTTCError(): Int? = errors.purchasePriceTTC

    @Bindable
    fun getSteadyPurchasePriceHTError(): Int? = errors.steadyPurchasePriceHT

    @Bindable
    fun getSteadyPurchasePriceTTCError(): Int? = errors.steadyPurchasePriceTTC

    @Bindable
    fun getMargeError(): Int? = errors.marge

    @Bindable
    fun getMargeHalfWholeError(): Int? = errors.margeHalfWhole

    @Bindable
    fun getMargeWholeError(): Int? = errors.margeWhole

    @Bindable
    fun getSellPriceDetailHTError(): Int? = errors.sellPriceDetailHT

    @Bindable
    fun getSellPriceWholeHTError(): Int? = errors.sellPriceWholeHT

    @Bindable
    fun getSellPrice3HTError(): Int? = errors.sellPrice3HT

    @Bindable
    fun getSellPriceDetailTTCError(): Int? = errors.sellPriceDetailTTC

    @Bindable
    fun getSellPriceWholeTTCError(): Int? = errors.sellPriceWholeTTC

    @Bindable
    fun getSellPrice3TTCError(): Int? = errors.sellPrice3TTC

    @Bindable
    fun getPhotoError(): Int? = errors.photo

    @Bindable
    fun getQuantityPerPackageError(): Int? = errors.quantityPerPackage

    @Bindable
    fun getPromotionError(): Int? = errors.promotion

    @Bindable
    fun getBrandError(): Int? = errors.brand

    @Bindable
    fun getTvaError(): Int? = errors.tva

    fun isBarcodeValid(setMessage: Boolean): Boolean {
        val mBarcode = fields.barcode.value ?: ""
        return if (!mBarcode.isEmptyOrBlank() && mBarcode.length >= 11) {
            errors.barcode = null
            notifyPropertyChanged(BR.productValid)
            true
        } else {
            if (setMessage) {
                errors.barcode = R.string.invalid_barcode
                notifyPropertyChanged(BR.productValid)
            }
            false
        }
    }

    fun isReferenceValid(setMessage: Boolean): Boolean {
        val mReference = fields.reference.value ?: ""
        return if (!mReference.isEmptyOrBlank()) {
            errors.reference = null
            notifyPropertyChanged(BR.productValid)
            true
        } else {
            if (setMessage) {
                errors.reference = R.string.invalid_reference
                notifyPropertyChanged(BR.productValid)
            }
            false
        }
    }

    fun isDesignationValid(setMessage: Boolean): Boolean {
        val mDesignation = fields.designation.value ?: ""
        return if (!mDesignation.isEmptyOrBlank()) {
            errors.designation = null
            notifyPropertyChanged(BR.productValid)
            true
        } else {
            if (setMessage) {
                errors.designation = R.string.invalid_designation
                notifyPropertyChanged(BR.productValid)
            }
            false
        }
    }

    fun isBrandNameValid(): Boolean {
        val mBrandName = fields.brandName.value ?: ""
        return !mBrandName.isEmptyOrBlank()
    }

}