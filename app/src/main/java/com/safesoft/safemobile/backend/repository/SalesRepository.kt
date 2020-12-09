package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.local.dao.SalesDao
import com.safesoft.safemobile.backend.db.local.entity.SaleLines
import com.safesoft.safemobile.backend.db.local.entity.Sales
import javax.inject.Inject

class SalesRepository @Inject constructor(private val salesDao: SalesDao) {
    fun getAllSales() = salesDao.getAllSales()

    fun searchSales(query: String) = salesDao.searchSales(query)

    fun insertNewSale(sale: Sales) = salesDao.insertNewSale(sale)

    fun insertSaleLines(vararg saleLines: SaleLines) =
        salesDao.insertSaleLines(*saleLines)

}