package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.local.dao.InventoryDao
import com.safesoft.safemobile.backend.db.local.entity.Inventories
import com.safesoft.safemobile.backend.db.local.entity.InventoryLines
import javax.inject.Inject

class InventoryRepository @Inject constructor(private val inventoryDao: InventoryDao) {

    fun getAllInventories() = inventoryDao.getAllInventories()

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
}