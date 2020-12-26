package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.api.service.PurchaseService
import com.safesoft.safemobile.backend.api.wrapper.UpdatePurchaseWrapper
import com.safesoft.safemobile.backend.db.local.dao.PurchasesDao
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.local.entity.PurchaseLines
import com.safesoft.safemobile.backend.db.local.entity.Purchases
import com.safesoft.safemobile.backend.db.remote.dao.RemotePurchaseDao
import javax.inject.Inject

class PurchasesRepository @Inject constructor(
    private val purchasesDao: PurchasesDao,
    private val purchasesService: PurchaseService,
    private val remotePurchaseDao: RemotePurchaseDao,
) {
    fun getAllPurchases() = purchasesDao.getAllPurchases()

    fun getAllPurchasesWithLines() = purchasesDao.getAllPurchasesWithLines()

    fun searchPurchases(query: String) = purchasesDao.searchPurchases(query)

    fun insertNewPurchase(purchase: Purchases) = purchasesDao.insertNewPurchase(purchase)

    fun insertPurchaseLines(vararg purchaseLines: PurchaseLines) =
        purchasesDao.insertPurchaseLines(*purchaseLines)

    fun getAllNewPurchasesWithAllInfo() = purchasesDao.getAllNewPurchasesWithAllInfo()

    fun markAllPurchasesAsSync() = purchasesDao.markAllPurchasesAsSync()

    fun updatePurchases(purchases: UpdatePurchaseWrapper) =
        purchasesService.updatePurchases(purchases)

    fun insertPurchasesInRemoteDB(vararg purchases: AllAboutAPurchase) =
        remotePurchaseDao.insert(*purchases)


}