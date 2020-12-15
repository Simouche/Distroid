package com.safesoft.safemobile.backend.repository

import android.content.SharedPreferences
import java.util.*
import javax.inject.Inject

class PreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val sharedPreferencesEditor = sharedPreferences.edit()

    fun getAutomaticSync() = sharedPreferences.getBoolean("auto_sync", true)

    fun setAutomaticSync(value: Boolean) =
        sharedPreferencesEditor.putBoolean("auto_sync", value).commit()

    fun getAutomaticSyncDuration() = sharedPreferences.getInt("auto_sync_duration", 0)

    fun setAutomaticSyncDuration(value: Int) =
        sharedPreferencesEditor.putInt("auto_sync_duration", value).commit()

    fun getSyncClientsModule() = sharedPreferences.getBoolean("sync_clients_module", true)

    fun setSyncClientsModule(value: Boolean) =
        sharedPreferencesEditor.putBoolean("sync_clients_module", value).commit()

    fun getSyncProvidersModule() = sharedPreferences.getBoolean("sync_providers_module", true)

    fun setSyncProviderModule(value: Boolean) =
        sharedPreferencesEditor.putBoolean(("sync_providers_module"), value).commit()

    fun getSyncProductsModule() = sharedPreferences.getBoolean("sync_products_module", true)

    fun setSyncProductsModule(value: Boolean) =
        sharedPreferencesEditor.putBoolean("sync_products_module", value).commit()

    fun getSyncSalesModule() = sharedPreferences.getBoolean("sync_sales_module", true)

    fun setSyncSalesModule(value: Boolean) =
        sharedPreferencesEditor.putBoolean("sync_sales_module", value).commit()

    fun getSyncPurchasesModule() = sharedPreferences.getBoolean("sync_purchases_module", true)

    fun setSyncPurchasesModule(value: Boolean) =
        sharedPreferencesEditor.putBoolean("sync_purchases_module", value).commit()

    fun getSyncInventoriesModule() = sharedPreferences.getBoolean("sync_inventories_module", true)

    fun setSyncInventoriesModule(value: Boolean) =
        sharedPreferencesEditor.putBoolean("sync_inventories_module", value).commit()

    fun getSyncTrackingModule() = sharedPreferences.getBoolean("sync_tracking_module", true)

    fun setSyncTrackingModule(value: Boolean) =
        sharedPreferencesEditor.putBoolean("sync_tracking_module", value).commit()

    fun getLanguage() = sharedPreferences.getString("language", "fr")

    fun setLanguage(value: String) = sharedPreferencesEditor.putString("language", value).commit()

    fun getLocalServerIp() = sharedPreferences.getString("local_server_ip", "192.168.1.18")

    fun setLocalServerIp(value: String) =
        sharedPreferencesEditor.putString("local_server_ip", value).commit()

    fun getDBPath(): String {
        var path = sharedPreferences.getString("local_db_path", "D:\\PME PRO")
        path = if (!path?.toLowerCase(Locale.getDefault())?.endsWith("fdb")!!) "$path.FDB" else path
        return path
    }

    fun setDBPath(value: String) =
        sharedPreferencesEditor.putString("local_db_path", value.replace("/", "\\")).commit()


    fun getWarehouseCode() = sharedPreferences.getString("warehouse_code", "0")!!

    fun setWareHouseCode(value: String) = sharedPreferencesEditor.putString("warehouse_code", value)
}