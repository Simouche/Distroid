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


    fun getWarehouseCode() = sharedPreferences.getString("warehouse_code", null)

    fun setWareHouseCode(value: String) =
        sharedPreferencesEditor.putString("warehouse_code", value).commit()

    fun getTarifactionMode() = sharedPreferences.getString("tarifaction_mode", null)

    fun setTarifactionMode(value: String) =
        sharedPreferencesEditor.putString("tarifaction_mode", value).commit()

    fun getTvaValue() = sharedPreferences.getFloat("tva_value", 19.0f).toDouble()

    fun setTvaValue(value: Double) =
        sharedPreferencesEditor.putFloat("tva_value", value.toFloat()).commit()

    fun getUseFlash(): Boolean = sharedPreferences.getBoolean("use_flash", false)

    fun setUseFlash(value: Boolean) =
        sharedPreferencesEditor.putBoolean("use_flash", value).commit()

    fun getAutoFocus(): Boolean = sharedPreferences.getBoolean("use_auto_focus", true)

    fun setAutoFocus(value: Boolean) =
        sharedPreferencesEditor.putBoolean("use_auto_focus", value).commit()

    fun getDBUsername() = sharedPreferences.getString("db_username", "SYSDBA")

    fun getDBPassword() = sharedPreferences.getString("db_password", "masterkey")

    fun getActiveModules() = sharedPreferences.getStringSet("modules", setOf("P", "S", "I"))

    fun setActiveModules(modules: Set<String>) =
        sharedPreferencesEditor.putStringSet("modules", modules)

    fun getSelectedPrinter() = sharedPreferences.getString("selected_printer", "I")

    fun setSelectedPrinter(value: String) =
        sharedPreferencesEditor.putString("selected_printer", value).commit()

    fun getShowHeader() = sharedPreferences.getBoolean("show_header", false)

    fun setShowHeader(value: Boolean) =
        sharedPreferencesEditor.putBoolean("show_header", value).commit()

    fun getShowFooter() = sharedPreferences.getBoolean("show_footer", false)

    fun setShowFooter(value: Boolean) =
        sharedPreferencesEditor.putBoolean("show_footer", value).commit()

    fun allowNegativeStock() = sharedPreferences.getBoolean("allow_negative_stock", false)

    fun setAllowNegativeStock(value: Boolean) =
        sharedPreferencesEditor.putBoolean("allow_negative_stock", value).commit()

    fun getEnterpriseName() = sharedPreferences.getString("enterprise_name", "")

    fun setEnterpriseName(value: String) =
        sharedPreferencesEditor.putString("enterprise_name", value).commit()

    fun getEnterpriseAddress() = sharedPreferences.getString("enterprise_address", "")

    fun setEnterpriseAddress(value: String) =
        sharedPreferencesEditor.putString("enterprise_address", value).commit()

    fun getEnterprisePhone() = sharedPreferences.getString("enterprise_phone", "")

    fun setEnterprisePhone(value: String) =
        sharedPreferencesEditor.putString("enterprise_phone", value).commit()

    fun getEnterpriseFooter() = sharedPreferences.getString("enterprise_footer", "")

    fun setEnterpriseFooter(value: String) =
        sharedPreferencesEditor.putString("enterprise_footer", value).commit()


}