package com.safesoft.safemobile.viewmodel

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepo
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import com.safesoft.safemobile.backend.db.remote.dao.RemoteProviderDao
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.backend.worker.ClearTablesWorker
import com.safesoft.safemobile.backend.worker.ClientsWorker
import com.safesoft.safemobile.backend.worker.product.ProductsWorker
import com.safesoft.safemobile.backend.worker.ProvidersWorker
import com.safesoft.safemobile.backend.worker.PurchaseWorker
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.coroutineContext

class SettingsViewModel @ViewModelInject constructor(
    application: Application,
    private val preferencesRepository: PreferencesRepository,
    private val remoteDBRepository: RemoteDBRepository,
    private val remoteProviderDao: RemoteProviderDao,
) : BaseAndroidViewModel(application) {

    val trigger = 0

    private val workManagerInstance: WorkManager = WorkManager.getInstance(getApplication())

    private val ids = mutableMapOf<String, UUID>()

    val ipAddress =
        MutableLiveData<String>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getLocalServerIp())
            }
        }

    val dbPath =
        MutableLiveData<String>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getDBPath())
            }
        }

    val warehouseCode =
        MutableLiveData<String>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getWarehouseCode())
            }
        }

    val isSyncing = MutableLiveData<Boolean>().apply { value = false }

    val autoSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getAutomaticSync())
            }
        }

    val syncDuration: MutableLiveData<Int> =
        MutableLiveData<Int>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getAutomaticSyncDuration())
            }
        }

    val clientsSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getSyncClientsModule())
            }
        }

    val providersSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getSyncProvidersModule())
            }
        }

    val productsSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getSyncProductsModule())
            }
        }

    val salesSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getSyncSalesModule())
            }
        }

    val purchasesSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getSyncPurchasesModule())
            }
        }

    val inventoriesSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getSyncInventoriesModule())
            }
        }

    val trackingSync: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                postValue(preferencesRepository.getSyncTrackingModule())
            }
        }


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

        var workContinuation: WorkContinuation? = null

        for ((i, work) in requestsArray.withIndex())
            workContinuation = if (i == 0)
                workManagerInstance.beginWith(work)
            else
                workContinuation?.then(work)

        workContinuation?.enqueue()
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


    fun testConnection(): LiveData<Resource<Boolean>> {
        val result = MutableLiveData<Resource<Boolean>>()
        enqueue(remoteDBRepository.checkConnection(), result)
        return result
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

    fun setServerIp(value: String) = preferencesRepository.setLocalServerIp(value)

    fun setDBPath(value: String) = preferencesRepository.setDBPath(value)


}