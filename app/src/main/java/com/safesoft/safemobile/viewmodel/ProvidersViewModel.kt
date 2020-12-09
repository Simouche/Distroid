package com.safesoft.safemobile.viewmodel

import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.safesoft.safemobile.backend.db.local.entity.FiscalData
import com.safesoft.safemobile.backend.db.local.entity.Location
import com.safesoft.safemobile.backend.db.local.entity.Providers
import com.safesoft.safemobile.backend.repository.ProvidersRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.forms.ProviderForm

class ProvidersViewModel @ViewModelInject constructor(
    private val providersRepository: ProvidersRepository,
    private val config: PagedList.Config,
    val providerForm: ProviderForm
) : BaseViewModel(), BaseFormOwner {
    private val tag = ProvidersViewModel::class.simpleName

    val searchQuery = MutableLiveData<String>()
    val providersList: LiveData<PagedList<Providers>> =
        providersRepository.getAllProviders().toLiveData(config = config)


    fun search(query: String): LiveData<PagedList<Providers>> =
        providersRepository.searchProvider(query).toLiveData(config = config)


    val onFocusCode: View.OnFocusChangeListener = View.OnFocusChangeListener { view, focused ->
        run {
            val et = view as EditText
            if (et.text.isNotEmpty() && !focused)
                providerForm.isCodeValid(true)
        }
    }

    fun codeDoubleTap(): Boolean {
        Log.d(tag, "Code double tapped!")
        providerForm.fields.code.value = Providers.generateProviderCode()
        return true
    }

    fun searchFlow(query: String): LiveData<Resource<List<Providers>>> {
        val data = MutableLiveData<Resource<List<Providers>>>()
        enqueue(providersRepository.searchProviderFlow(query), data)
        return data
    }


    private fun saveProvider(vararg providers: Providers): LiveData<Resource<Int>> {
        val data = MutableLiveData<Resource<Int>>()
        enqueue(providersRepository.addProviders(*providers), data)
        return data
    }

    fun saveProvider(): LiveData<Resource<Int>> {
        val fields = providerForm.fields
        val provider = Providers(
            0,
            code = fields.code.value!!,
            name = fields.name.value,
            activity = fields.activity.value,
            address=fields.address.value,
            commune = fields.commune.value,
            contact = fields.contact.value,
            phones = fields.phones.value,
            faxes = fields.faxes.value,
            rib = fields.rib.value,
            webSite = fields.webSite.value,
            sold = fields.initialSold.value,
            note = fields.note.value,
            client = fields.client.value,
            fiscalData = FiscalData(
                fields.registreCommerce.value,
                fields.articleFiscale.value,
                fields.identifiantFiscale.value,
                fields.identifiantStatistic.value
            ),
            location = Location(
                fields.longitude.value ?: 0.0,
                fields.latitude.value ?: 0.0
            ),
            synched = false,
            inApp = true
        )
        return saveProvider(provider)
    }

    override fun reInitFields() {
        val fields = providerForm.fields
        fields.code.value = null
        fields.name.value = null
        fields.activity.value = null
        fields.address.value = null
        fields.commune.value = null
        fields.contact.value = null
        fields.phones.value = null
        fields.faxes.value = null
        fields.rib.value = null
        fields.webSite.value = null
        fields.initialSold.value = null
        fields.note.value = null
        fields.client.value = null
        fields.registreCommerce.value = null
        fields.articleFiscale.value = null
        fields.identifiantFiscale.value = null
        fields.identifiantStatistic.value = null
        fields.longitude.value = 0.0
        fields.latitude.value = 0.0
    }

}