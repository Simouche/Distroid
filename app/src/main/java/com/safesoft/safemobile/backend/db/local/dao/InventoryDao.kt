package com.safesoft.safemobile.backend.db.local.dao

import androidx.paging.DataSource
import androidx.room.*
import com.safesoft.safemobile.backend.db.local.entity.Inventories
import com.safesoft.safemobile.backend.db.local.entity.InventoryLines
import com.safesoft.safemobile.backend.db.local.entity.InventoryWithLines
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface InventoryDao {

    @Transaction
    @Query("SELECT * FROM inventories")
    fun getAllInventories(): DataSource.Factory<Int, InventoryWithLines>

    @Transaction
    @Query("SELECT * FROM inventories WHERE SYNC=0")
    fun getAllInventoriesSync(): Single<List<InventoryWithLines>>

    @Query("UPDATE inventories SET SYNC=1")
    fun markAllInventoriesAsSync(): Completable

    @Update
    fun updateInventory(vararg inventories: Inventories): Completable

    @Insert
    fun insertInventory(inventories: Inventories): Single<Long>

    @Delete
    fun deleteInventory(vararg inventories: Inventories): Completable

    @Update
    fun updateInventoryLines(vararg inventoryLines: InventoryLines): Completable

    @Insert
    fun insertInventoryLines(vararg inventoryLines: InventoryLines): Single<List<Long>>

    @Delete
    fun deleteInventoryLines(vararg inventoryLines: InventoryLines): Completable

    @Transaction
    @Query("SELECT * FROM inventories WHERE LABEL LIKE '%'||:query||'%' OR LABEL LIKE '%'||:query||'%'")
    fun searchInventory(query: String): DataSource.Factory<Int, InventoryWithLines>

}