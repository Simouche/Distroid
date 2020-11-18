package com.safesoft.safemobile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.backend.repository.PreferencesRepository

class SettingsViewModel @ViewModelInject constructor(
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel() {

    val autoSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getAutomaticSync() }

    val syncDuration: MutableLiveData<Int> =
        MutableLiveData<Int>().apply { value = preferencesRepository.getAutomaticSyncDuration() }

    val clientsSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getSyncClientsModule() }

    val providersSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getSyncProvidersModule() }

    val productsSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getSyncProductsModule() }

    val salesSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getSyncSalesModule() }

    val purchasesSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getSyncPurchasesModule() }

    val inventoriesSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            value = preferencesRepository.getSyncInventoriesModule()
        }

    val trackingSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = preferencesRepository.getSyncTrackingModule() }


}