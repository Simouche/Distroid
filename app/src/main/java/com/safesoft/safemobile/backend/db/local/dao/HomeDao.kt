package com.safesoft.safemobile.backend.db.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.safesoft.safemobile.backend.model.DashBoard
import io.reactivex.Single

@Dao
interface HomeDao {

    @Query("SELECT COUNT(*) FROM purchases WHERE done = 0")
    fun getOpenPurchases(): Int

    @Query("SELECT COUNT(*) FROM purchases WHERE done = 1")
    fun getValidatedPurchases(): Int

    @Query("SELECT COUNT(*) FROM purchases")
    fun getPurchasesCount(): Int

    @Query("SELECT COUNT(*) FROM products WHERE quantity = 0")
    fun getProductsOutOfStockCount(): Int

    @Query("SELECT COUNT(*) FROM products")
    fun getAllProductsCount(): Int

    @Query("SELECT COUNT(*) FROM clients")
    fun getAllClientsCount(): Int

    @Query("SELECT COUNT(*) FROM providers")
    fun getAllProvidersCount(): Int

    @Transaction
    fun getCountTransaction(): DashBoard {
        return DashBoard(
            openPurchases = getOpenPurchases(),
            validatedPurchases = getValidatedPurchases(),
            totalPurchases = getPurchasesCount(),
            productsOutOfStock = getProductsOutOfStockCount(),
            productsTotal = getAllProductsCount(),
            totalClients = getAllClientsCount(),
            totalProviders = getAllProvidersCount()
        )
    }

    fun getCounts(): Single<DashBoard> {
        return Single.fromCallable { getCountTransaction() }
    }
}