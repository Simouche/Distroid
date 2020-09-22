package com.safesoft.safemobile.backend.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.safesoft.safemobile.backend.db.entity.PurchaseWithLines
import com.safesoft.safemobile.backend.db.entity.Purchases
import io.reactivex.Flowable

@Dao
interface PurchasesDao {

    @Query("SELECT * FROM purchases")
    fun getAllPurchases(): DataSource.Factory<Int, Purchases>

    @Query("SELECT * FROM purchases JOIN providers ON purchases.PROVIDER = providers.PROVIDER_ID WHERE PURCHASE_NUMBER LIKE :query OR INVOICE_NUMBER LIKE :query OR DATE LIKE :query OR providers.NAME LIKE :query")
    fun searchPurchases(query: String): DataSource.Factory<Int, Purchases>

    @Transaction
    @Query("SELECT * FROM Purchases")
    fun getAllPurchasesWithLines(): Flowable<List<PurchaseWithLines>>

}