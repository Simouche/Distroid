package com.safesoft.safemobile.backend.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.safesoft.safemobile.backend.db.dao.*
import com.safesoft.safemobile.backend.db.entity.*

@Database(
    entities = [Users::class, Purchases::class, Sales::class, PurchaseLines::class,
        Providers::class, Barcodes::class, Products::class, ExpirationDates::class, Brands::class,
        ProductProviderCrossRef::class, Clients::class, ClientPayments::class, ProviderPayments::class],
    version = 1
)
abstract class SafeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun purchaseDao(): PurchasesDao
    abstract fun salesDao(): SalesDao
    abstract fun productsDao(): ProductsDao
    abstract fun providersDao(): ProvidersDao
    abstract fun clientsDao(): ClientsDao
    abstract fun homeDao(): HomeDao


}