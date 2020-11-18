package com.safesoft.safemobile.backend.repository

import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getAutomaticSync() = sharedPreferences.getBoolean("auto_sync", true)

    fun getAutomaticSyncDuration() = sharedPreferences.getInt("auto_sync_duration", 0)

    fun getSyncClientsModule() = sharedPreferences.getBoolean("sync_clients_module", true)
    fun getSyncProvidersModule() = sharedPreferences.getBoolean("sync_providers_module", true)
    fun getSyncProductsModule() = sharedPreferences.getBoolean("sync_products_module", true)
    fun getSyncSalesModule() = sharedPreferences.getBoolean("sync_sales_module", true)
    fun getSyncPurchasesModule() = sharedPreferences.getBoolean("sync_purchases_module", true)
    fun getSyncInventoriesModule() = sharedPreferences.getBoolean("sync_inventories_module", true)
    fun getSyncTrackingModule() = sharedPreferences.getBoolean("sync_tracking_module", true)
}