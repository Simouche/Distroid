package com.safesoft.safemobile.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class PurchaseFields @Inject constructor() {
    var purchaseNumber = MutableLiveData<String>()
    var invoiceNumber = MutableLiveData<String>()
    var date = MutableLiveData<String>()
    var provider = MutableLiveData<Long>()
    var providerName = MutableLiveData<String>()
    var productsCount = MutableLiveData<Int>()
    var totalHT = MutableLiveData<Double>()
    var totalTTC = MutableLiveData<Double>()
    var tva = MutableLiveData<Double>()
    var stamp = MutableLiveData<Double>()
    var reglement = MutableLiveData<String>()
    var totalQuantity = MutableLiveData<Double>()
    var note = MutableLiveData<String>()
    var done = MutableLiveData<Boolean>()
    var payment = MutableLiveData<Double>()
}

class PurchaseErrorFields @Inject constructor() {
    var purchaseNumber: Int? = null
    var invoiceNumber: Int? = null
    var date: Int? = null
    var provider: Int? = null
    var providerName: Int? = null
    var productsCount: Int? = null
    var totalHT: Int? = null
    var totalTTC: Int? = null
    var tva: Int? = null
    var stamp: Int? = null
    var reglement: Int? = null
    var totalQuantity: Int? = null
    var note: Int? = null
    var done: Int? = null
    var payment: Int? = null
}

class PurchaseForm @Inject constructor(
    val fields: PurchaseFields,
    val errors: PurchaseErrorFields
) : BaseObservable() {

    @Bindable
    fun isValid(): Boolean {
        return true
    }

    fun isProviderValid(setMessage: Boolean): Boolean {
        return true
    }

    fun isProductValid(setMessage: Boolean): Boolean {
        return true
    }

    @Bindable
    fun getPurchaseNumber(): Int? = errors.purchaseNumber

    @Bindable
    fun getInvoiceNumber(): Int? = errors.invoiceNumber

    @Bindable
    fun getDate(): Int? = errors.date

    @Bindable
    fun getProvider(): Int? = errors.provider

    @Bindable
    fun getProviderName(): Int? = errors.providerName

    @Bindable
    fun getProductsCount(): Int? = errors.productsCount

    @Bindable
    fun getTotalHT(): Int? = errors.totalHT

    @Bindable
    fun getTotalTTC(): Int? = errors.totalTTC

    @Bindable
    fun getTva(): Int? = errors.tva

    @Bindable
    fun getStamp(): Int? = errors.stamp

    @Bindable
    fun getReglement(): Int? = errors.reglement

    @Bindable
    fun getTotalQuantity(): Int? = errors.totalQuantity

    @Bindable
    fun getNote(): Int? = errors.note

    @Bindable
    fun getDone(): Int? = errors.done

    @Bindable
    fun getPayment(): Int? = errors.payment
}


class SalesFields @Inject constructor() {
    var saleNumber = MutableLiveData<String>()
    var invoiceNumber = MutableLiveData<String>()
    var date = MutableLiveData<String>()
    var client = MutableLiveData<Long>()
    var clientName = MutableLiveData<String>()
    var productsCount = MutableLiveData<Int>()
    var totalHT = MutableLiveData<Double>()
    var totalTTC = MutableLiveData<Double>()
    var tva = MutableLiveData<Double>()
    var stamp = MutableLiveData<Double>()
    var reglement = MutableLiveData<String>()
    var totalQuantity = MutableLiveData<Double>()
    var note = MutableLiveData<String>()
    var done = MutableLiveData<Boolean>()
    var payment = MutableLiveData<Double>()
}

class SalesErrorFields @Inject constructor() {
    var saleNumber: Int? = null
    var invoiceNumber: Int? = null
    var date: Int? = null
    var client: Int? = null
    var clientName: Int? = null
    var productsCount: Int? = null
    var totalHT: Int? = null
    var totalTTC: Int? = null
    var tva: Int? = null
    var stamp: Int? = null
    var reglement: Int? = null
    var totalQuantity: Int? = null
    var note: Int? = null
    var done: Int? = null
    var payment: Int? = null
}

class SalesForm @Inject constructor(val fields: SalesFields, val errors: SalesErrorFields) :
    BaseObservable() {
    @Bindable
    fun isValid(): Boolean {
        return true
    }

    fun isClientValid(setMessage: Boolean): Boolean {
        return true
    }

    fun isProductValid(setMessage: Boolean): Boolean {
        return true
    }

    @Bindable
    fun getSaleNumber(): Int? = errors.saleNumber

    @Bindable
    fun getInvoiceNumber(): Int? = errors.invoiceNumber

    @Bindable
    fun getDate(): Int? = errors.date

    @Bindable
    fun getProvider(): Int? = errors.client

    @Bindable
    fun getProviderName(): Int? = errors.clientName

    @Bindable
    fun getProductsCount(): Int? = errors.productsCount

    @Bindable
    fun getTotalHT(): Int? = errors.totalHT

    @Bindable
    fun getTotalTTC(): Int? = errors.totalTTC

    @Bindable
    fun getTva(): Int? = errors.tva

    @Bindable
    fun getStamp(): Int? = errors.stamp

    @Bindable
    fun getReglement(): Int? = errors.reglement

    @Bindable
    fun getTotalQuantity(): Int? = errors.totalQuantity

    @Bindable
    fun getNote(): Int? = errors.note

    @Bindable
    fun getDone(): Int? = errors.done

    @Bindable
    fun getPayment(): Int? = errors.payment
}