package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.local.dao.SalesDao
import com.safesoft.safemobile.backend.db.local.entity.AllAboutASale
import com.safesoft.safemobile.backend.db.local.entity.SaleLines
import com.safesoft.safemobile.backend.db.local.entity.Sales
import com.safesoft.safemobile.backend.db.remote.dao.RemoteSalesDao
import javax.inject.Inject

class SalesRepository @Inject constructor(
    private val salesDao: SalesDao,
    private val remoteSalesDao: RemoteSalesDao
) {
    fun getAllSales() = salesDao.getAllSales()

    fun searchSales(query: String) = salesDao.searchSales(query)

    fun insertNewSale(sale: Sales) = salesDao.insertNewSale(sale)

    fun insertSaleLines(vararg saleLines: SaleLines) =
        salesDao.insertSaleLines(*saleLines)

    fun getAllNewSalesWithAllInfo() = salesDao.getAllNewSalesWithAllInfo()

    fun getSaleWithAllInfoById(id: Long) = salesDao.getSaleWithAllInfoById(id)

    fun markAllSalesAsSync() = salesDao.markAllSalesAsSync()

    fun insertSalesInRemoteDB(vararg sales: AllAboutASale) =
        remoteSalesDao.insert(*sales)

}