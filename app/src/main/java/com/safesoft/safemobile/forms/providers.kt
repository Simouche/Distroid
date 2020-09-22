package com.safesoft.safemobile.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.BR
import com.safesoft.safemobile.R
import javax.inject.Inject

class ProviderFields @Inject constructor() {
    var code = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var activity = MutableLiveData<String>()
    var address = MutableLiveData<String>()
    var commune = MutableLiveData<Long>()
    var contact = MutableLiveData<String>()
    var phones = MutableLiveData<String>()
    var faxes = MutableLiveData<String>()
    var rib = MutableLiveData<String>()
    var webSite = MutableLiveData<String>()
    var initialSold = MutableLiveData<Double>()
    var note = MutableLiveData<String>()
    var client = MutableLiveData<Long>()
    val registreCommerce = MutableLiveData<String>()
    val articleFiscale = MutableLiveData<String>()
    val identifiantFiscale = MutableLiveData<String>()
    val identifiantStatistic = MutableLiveData<String>()
    val longitude = MutableLiveData<Double>()
    val latitude = MutableLiveData<Double>()
}

class ProviderErrorFields @Inject constructor() {
    var code: Int? = null
    var name: Int? = null
    var activity: Int? = null
    var address: Int? = null
    var commune: Int? = null
    var contact: Int? = null
    var phones: Int? = null
    var faxes: Int? = null
    var rib: Int? = null
    var webSite: Int? = null
    var initialSold: Int? = null
    var note: Int? = null
    var client: Int? = null
    val registreCommerce: Int? = null
    val articleFiscale: Int? = null
    val identifiantFiscale: Int? = null
    val identifiantStatistic: Int? = null
    val longitude: Int? = null
    val latitude: Int? = null
}

class ProviderForm @Inject constructor(
    val fields: ProviderFields,
    private val errors: ProviderErrorFields
) : BaseObservable() {

    @Bindable
    fun isValid(): Boolean {
        return true
    }

    fun isCodeValid(setMessage: Boolean): Boolean {
        val mcode = fields.code.value!!
        return if (mcode != null && mcode.length >= 5) {
            errors.code = null
            notifyPropertyChanged(BR.valid)
            true
        } else {
            if (setMessage) {
                errors.code = R.string.invalid_code
                notifyPropertyChanged(BR.valid)
            }
            false
        }
    }

    @Bindable
    fun getCodeError(): Int? = errors.code

    @Bindable
    fun getNameError(): Int? = errors.name

    @Bindable
    fun getActivityError(): Int? = errors.activity

    @Bindable
    fun getAddressError(): Int? = errors.address

    @Bindable
    fun getCommuneError(): Int? = errors.commune

    @Bindable
    fun getContactError(): Int? = errors.contact

    @Bindable
    fun getPhonesError(): Int? = errors.phones

    @Bindable
    fun getFaxesError(): Int? = errors.faxes

    @Bindable
    fun getRibError(): Int? = errors.rib

    @Bindable
    fun getWebSiteError(): Int? = errors.webSite

    @Bindable
    fun getInitialSoldError(): Int? = errors.initialSold

    @Bindable
    fun getNoteError(): Int? = errors.note

    @Bindable
    fun getClienterror(): Int? = errors.client

    @Bindable
    fun getRegistreCommerceError(): Int? = errors.registreCommerce

    @Bindable
    fun getArticleFiscaleError(): Int? = errors.articleFiscale

    @Bindable
    fun getIdentifiantFiscaleError(): Int? = errors.identifiantFiscale

    @Bindable
    fun getIdentifiantStatisticError(): Int? = errors.identifiantStatistic

    @Bindable
    fun getLongitudeError(): Int? = errors.longitude

    @Bindable
    fun getLatitudeError(): Int? = errors.latitude

}