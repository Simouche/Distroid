package com.safesoft.safemobile.backend.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.safesoft.safemobile.backend.db.converter.TimeConverter
import com.safesoft.safemobile.backend.db.dao.*
import com.safesoft.safemobile.backend.db.entity.*

@Database(
    entities = [Users::class, Purchases::class, Sales::class, PurchaseLines::class, SaleLines::class,
        Providers::class, Barcodes::class, Products::class, ExpirationDates::class, Brands::class,
        ProductProviderCrossRef::class, Clients::class, ClientPayments::class, ProviderPayments::class,
        Inventories::class, InventoryLines::class],
    version = 1
)
@TypeConverters(TimeConverter::class)
abstract class SafeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun purchaseDao(): PurchasesDao
    abstract fun salesDao(): SalesDao
    abstract fun productsDao(): ProductsDao
    abstract fun providersDao(): ProvidersDao
    abstract fun clientsDao(): ClientsDao
    abstract fun homeDao(): HomeDao
    abstract fun inventoryDao(): InventoryDao

}