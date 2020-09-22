package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.dao.SalesDao
import javax.inject.Inject

class SalesRepository @Inject constructor(private val salesDao: SalesDao) {
    fun getAllSales() = salesDao.getAllSales()

    fun searchSales(query: String) = salesDao.searchPurchases(query)
}