package com.safesoft.safemobile.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import com.safesoft.safemobile.backend.worker.ClearTablesWorker
import com.safesoft.safemobile.backend.worker.ClientsWorker
import com.safesoft.safemobile.backend.worker.product.ProductsWorker
import com.safesoft.safemobile.backend.worker.ProvidersWorker
import com.safesoft.safemobile.backend.worker.PurchaseWorker
import com.safesoft.safemobile.backend.worker.product.UpdateProductsWorker
import java.util.*

class SettingsViewModel @ViewModelInject constructor(
    application: Application,
    private val preferencesRepository: PreferencesRepository
) : BaseAndroidViewModel(application) {

    private val workManagerInstance: WorkManager = WorkManager.getInstance(getApplication())

    private val ids = mutableMapOf<String, UUID>()

    val ipAddress = MutableLiveData<String>().apply { value = preferencesRepository.getServerIp() }

    val warehouseCode =
        MutableLiveData<String>().apply { value = preferencesRepository.getWarehouseCode() }

    val isSyncing = MutableLiveData<Boolean>().apply { value = false }

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


    fun syncNowFab() {
        val requestsArray = mutableListOf<OneTimeWorkRequest>()
        val constraints: Constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        if (clientsSync.value != false) {
            val request = OneTimeWorkRequestBuilder<ClientsWorker>().setConstraints(constraints)
                .addTag("clients_sync").build()
            ids["clients"] = request.id
            requestsArray.add(request)
        }

        if (providersSync.value != false) {
            val request = OneTimeWorkRequestBuilder<ProvidersWorker>().setConstraints(constraints)
                .addTag("providers_sync").build()
            ids["providers"] = request.id
            requestsArray.add(request)
        }

        if (productsSync.value != false) {
            val request = OneTimeWorkRequestBuilder<ProductsWorker>().setConstraints(constraints)
                .addTag("products_sync").build()
            ids["product"] = request.id
            requestsArray.add(request)
        }
/*
        if (viewModel.clientsSync.value != false)
            requestsArray.add(OneTimeWorkRequestBuilder<ClientsWorker>().build())

        if (viewModel.clientsSync.value != false)
            requestsArray.add(OneTimeWorkRequestBuilder<ClientsWorker>().build())

        if (viewModel.clientsSync.value != false)
            requestsArray.add(OneTimeWorkRequestBuilder<ClientsWorker>().build())

        if (viewModel.clientsSync.value != false)
            requestsArray.add(OneTimeWorkRequestBuilder<ClientsWorker>().build())

        if (viewModel.clientsSync.value != false)
            requestsArray.add(OneTimeWorkRequestBuilder<ClientsWorker>().build())*/
        workManagerInstance.beginWith(requestsArray).enqueue()
    }

    fun updateServer() {
        workManagerInstance.enqueue(
            OneTimeWorkRequestBuilder<PurchaseWorker>()
                .addTag("purchases_update").build()
        )
    }

    fun reinitialize() {
        val request = OneTimeWorkRequestBuilder<ClearTablesWorker>().addTag("clear_db").build()
        ids["clearTables"] = request.id
        workManagerInstance.enqueue(request)
    }

    private fun observeWorkers() {
//        if (ids.containsKey("clients"))
//            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(ids["clients"]!!)
//                .observe(viewLifecycleOwner,
//                    {
//                        if (it != null) {
//                            if (it.state.isFinished)
//                                showNotification("Clients")
//                        }
//                    })
    }


    fun save() {

    }

    fun setAutomaticSync(value: Boolean) = preferencesRepository.setAutomaticSync(value)

    fun setAutomaticSyncDuration(value: Int) = preferencesRepository.setAutomaticSyncDuration(value)

    fun setSyncClientsModule(value: Boolean) = preferencesRepository.setSyncClientsModule(value)

    fun setSyncProviderModule(value: Boolean) = preferencesRepository.setSyncProviderModule(value)

    fun setSyncProductsModule(value: Boolean) = preferencesRepository.setSyncProductsModule(value)

    fun setSyncSalesModule(value: Boolean) = preferencesRepository.setSyncSalesModule(value)

    fun setSyncPurchasesModule(value: Boolean) = preferencesRepository.setSyncPurchasesModule(value)

    fun setSyncInventoriesModule(value: Boolean) =
        preferencesRepository.setSyncInventoriesModule(value)

    fun setSyncTrackingModule(value: Boolean) = preferencesRepository.setSyncTrackingModule(value)

    fun setServerIp(value: String) = preferencesRepository.setServerIp(value)


}