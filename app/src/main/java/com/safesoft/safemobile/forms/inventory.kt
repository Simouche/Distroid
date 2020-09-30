package com.safesoft.safemobile.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class InventoryFields @Inject constructor() {
    val name = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val label = MutableLiveData<String>()
    val productsCount = MutableLiveData<Int>()
    val htAmount = MutableLiveData<Double>()
    val TVAAmount = MutableLiveData<Double>()
    val newHTAmount = MutableLiveData<Double>()
    val newTVAAmount = MutableLiveData<Double>()
    val htDifference = MutableLiveData<Double>()
    val tvaDifference = MutableLiveData<Double>()
    val status = MutableLiveData<String>()
    val warehouse = MutableLiveData<Int>()
}

class InventoryErrorFields @Inject constructor() {
    val name: Int? = null
    val date: Int? = null
    val time: Int? = null
    val label: Int? = null
    val productsCount: Int? = null
    val htAmount: Int? = null
    val TVAAmount: Int? = null
    val newHTAmount: Int? = null
    val newTVAAmount: Int? = null
    val htDifference: Int? = null
    val tvaDifference: Int? = null
    val status: Int? = null
    val warehouse: Int? = null
}

class InventoryForm @Inject constructor(
    val fields: InventoryFields,
    val errors: InventoryErrorFields
) : BaseObservable() {

    @Bindable
    fun isValid(): Boolean {
        return true
    }


    @Bindable
    fun getName(): Int? = errors.name

    @Bindable
    fun getDate(): Int? = errors.date

    @Bindable
    fun getTime(): Int? = errors.time

    @Bindable
    fun getLabel(): Int? = errors.label

    @Bindable
    fun getProductsCount(): Int? = errors.productsCount

    @Bindable
    fun getHtAmount(): Int? = errors.htAmount

    @Bindable
    fun getTVAAmount(): Int? = errors.TVAAmount

    @Bindable
    fun getNewHTAmount(): Int? = errors.newHTAmount

    @Bindable
    fun getNewTVAAmount(): Int? = errors.newTVAAmount

    @Bindable
    fun getHtDifference(): Int? = errors.htDifference

    @Bindable
    fun getTvaDifference(): Int? = errors.tvaDifference

    @Bindable
    fun getStatus(): Int? = errors.status

    @Bindable
    fun getWarehouse(): Int? = errors.warehouse


}