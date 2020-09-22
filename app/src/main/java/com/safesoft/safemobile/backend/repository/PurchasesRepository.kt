package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.dao.PurchasesDao
import javax.inject.Inject

class PurchasesRepository @Inject constructor(private val purchasesDao: PurchasesDao) {
    fun getAllPurchases() = purchasesDao.getAllPurchases()

    fun getAllPurchasesWithLines() = purchasesDao.getAllPurchasesWithLines()

    fun searchPurchases(query: String) = purchasesDao.searchPurchases(query)
}