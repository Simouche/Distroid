package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.dao.PurchasesDao
import com.safesoft.safemobile.backend.db.entity.PurchaseLines
import com.safesoft.safemobile.backend.db.entity.Purchases
import javax.inject.Inject

class PurchasesRepository @Inject constructor(private val purchasesDao: PurchasesDao) {
    fun getAllPurchases() = purchasesDao.getAllPurchases()

    fun getAllPurchasesWithLines() = purchasesDao.getAllPurchasesWithLines()

    fun searchPurchases(query: String) = purchasesDao.searchPurchases(query)

    fun insertNewPurchase(purchase: Purchases) = purchasesDao.insertNewPurchase(purchase)

    fun insertPurchaseLines(vararg purchaseLines: PurchaseLines) =
        purchasesDao.insertPurchaseLines(*purchaseLines)

}