package com.safesoft.safemobile.backend.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.safesoft.safemobile.backend.db.entity.SaleLines
import com.safesoft.safemobile.backend.db.entity.Sales
import io.reactivex.Flowable
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


}