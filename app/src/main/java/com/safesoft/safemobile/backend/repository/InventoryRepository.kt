package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.local.dao.InventoryDao
import com.safesoft.safemobile.backend.db.local.entity.Inventories
import com.safesoft.safemobile.backend.db.local.entity.InventoryLines
import com.safesoft.safemobile.backend.db.local.entity.InventoryWithLines
import com.safesoft.safemobile.backend.db.remote.dao.RemoteInventoryDao
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao,
    private val remoteInventoryDao: RemoteInventoryDao
) {

    fun getAllInventories() = inventoryDao.getAllInventories()

    fun markAllInventoriesAsSync() = inventoryDao.markAllInventoriesAsSync()

    fun getAllInventoriesSync() = inventoryDao.getAllInventoriesSync()

    fun searchInventory(query: String) = inventoryDao.searchInventory(query)

    fun updateInventory(vararg inventories: Inventories) =
        inventoryDao.updateInventory(* inventories)

    fun insertInventory(inventories: Inventories) =
        inventoryDao.insertInventory(inventories)

    fun deleteInventory(vararg inventories: Inventories) =
        inventoryDao.deleteInventory(* inventories)

    fun updateInventoryLines(vararg inventoryLines: InventoryLines) =
        inventoryDao.updateInventoryLines(* inventoryLines)

    fun insertInventoryLines(vararg inventoryLines: InventoryLines) =
        inventoryDao.insertInventoryLines(* inventoryLines)

    fun deleteInventoryLines(vararg inventoryLines: InventoryLines) =
        inventoryDao.deleteInventoryLines(* inventoryLines)

    fun insertInventoryInRemoteDB(vararg inventories: InventoryWithLines) =
        remoteInventoryDao.insert(*inventories)
}