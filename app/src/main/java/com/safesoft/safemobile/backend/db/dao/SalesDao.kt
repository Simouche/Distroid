package com.safesoft.safemobile.backend.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.safesoft.safemobile.backend.db.entity.Sales

@Dao
interface SalesDao {
    @Query("SELECT * FROM sales")
    fun getAllSales(): DataSource.Factory<Int, Sales>

    @Query("SELECT * FROM sales JOIN clients ON sales.CLIENT = clients.CLIENT_ID WHERE SALE_NUMBER LIKE :query OR INVOICE_NUMBER LIKE :query OR DATE LIKE :query OR clients.NAME LIKE :query")
    fun searchPurchases(query: String): DataSource.Factory<Int, Sales>



}