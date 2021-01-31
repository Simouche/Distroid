package com.safesoft.safemobile.backend.db.local.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.safesoft.safemobile.backend.db.local.entity.AllAboutASale
import com.safesoft.safemobile.backend.db.local.entity.SaleLines
import com.safesoft.safemobile.backend.db.local.entity.Sales
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface SalesDao {
    @Query("SELECT * FROM sales")
    fun getAllSales(): DataSource.Factory<Int, Sales>

    @Query("SELECT * FROM sales JOIN clients ON sales.CLIENT = clients.CLIENT_ID WHERE SALE_NUMBER LIKE :query OR INVOICE_NUMBER LIKE :query OR DATE LIKE :query OR clients.NAME LIKE :query")
    fun searchSales(query: String): DataSource.Factory<Int, Sales>


//    @Transaction
//    @Query("SELECT * FROM Sales")
//    fun getAllSalesWithLines(): Flowable<List<SaleWithLines>>

    @Insert
    fun insertNewSale(Sale: Sales): Single<Long>

    @Insert
    fun insertSaleLines(vararg lines: SaleLines): Single<List<Long>>

    @Transaction
    @Query("SELECT * FROM sales WHERE SYNC = 0 AND DONE = 1")
    fun getAllNewSalesWithAllInfo(): Single<List<AllAboutASale>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    fun getSaleWithAllInfoById(id: Long): Single<AllAboutASale>

    @Query("UPDATE sales SET SYNC=1 WHERE DONE = 1")
    fun markAllSalesAsSync(): Completable
}