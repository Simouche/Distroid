package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.api.service.PurchaseService
import com.safesoft.safemobile.backend.api.wrapper.UpdatePurchaseWrapper
import com.safesoft.safemobile.backend.db.dao.PurchasesDao
import com.safesoft.safemobile.backend.db.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.entity.ProductWithBarcodes
import com.safesoft.safemobile.backend.db.entity.PurchaseLines
import com.safesoft.safemobile.backend.db.entity.Purchases
import javax.inject.Inject

class PurchasesRepository @Inject constructor(
    private val purchasesDao: PurchasesDao,
    private val purchasesService: PurchaseService
) {
    fun getAllPurchases() = purchasesDao.getAllPurchases()

    fun getAllPurchasesWithLines() = purchasesDao.getAllPurchasesWithLines()

    fun searchPurchases(query: String) = purchasesDao.searchPurchases(query)

    fun insertNewPurchase(purchase: Purchases) = purchasesDao.insertNewPurchase(purchase)

    fun insertPurchaseLines(vararg purchaseLines: PurchaseLines) =
        purchasesDao.insertPurchaseLines(*purchaseLines)

    fun getAllPurchasesWithAllInfo() = purchasesDao.getAllPurchasesWithAllInfo()

    fun updatePurchases(purchases: UpdatePurchaseWrapper) =
        purchasesService.updatePurchases(purchases)

}