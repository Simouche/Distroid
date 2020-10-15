package com.safesoft.safemobile.backend.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.safesoft.safemobile.backend.db.entity.Inventories
import com.safesoft.safemobile.backend.db.entity.InventoryLines
import com.safesoft.safemobile.backend.db.entity.InventoryWithLines
import io.reactivex.Single

@Dao
interface InventoryDao {

    @Transaction
    @Query("SELECT * FROM inventories")
    fun getAllInventories(): DataSource.Factory<Int, InventoryWithLines>

    @Update
    fun updateInventory(vararg inventories: Inventories)

    @Insert
    fun insertInventory(inventories: Inventories): Single<Long>

    @Delete
    fun deleteInventory(vararg inventories: Inventories)

    @Update
    fun updateInventoryLines(vararg inventoryLines: InventoryLines)

    @Insert
    fun insertInventoryLines(vararg inventoryLines: InventoryLines): Single<List<Long>>

    @Delete
    fun deleteInventoryLines(vararg inventoryLines: InventoryLines)

    @Transaction
    @Query("SELECT * FROM inventories WHERE LABEL LIKE '%'||:query||'%' OR LABEL LIKE '%'||:query||'%'")
    fun searchInventory(query: String): DataSource.Factory<Int, InventoryWithLines>

//    @Transaction
//    @Insert
//    fun insertAllInventory(inventory: InventoryWithLines)

}