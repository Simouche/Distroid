package com.safesoft.safemobile.backend.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.safesoft.safemobile.backend.db.entity.Clients
import com.safesoft.safemobile.backend.db.entity.ProviderWithProducts
import com.safesoft.safemobile.backend.db.entity.ProviderWithPurchases
import com.safesoft.safemobile.backend.db.entity.Providers
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ProvidersDao {
    @Query("SELECT * FROM providers")
    fun getAllProviders(): DataSource.Factory<Int, Providers>

    @Query("SELECT * FROM providers where PROVIDER_ID=:id")
    fun getProviderById(id: Long): Flowable<Providers>

    @Query("SELECT * FROM providers WHERE CODE LIKE '%'||:query||'%' OR NAME LIKE '%'||:query||'%' OR PHONES LIKE '%'||:query||'%'")
    fun searchProvider(query: String): DataSource.Factory<Int, Providers>

    @Query("SELECT * FROM providers WHERE CODE LIKE '%'||:query||'%' OR NAME LIKE '%'||:query||'%' OR PHONES LIKE '%'||:query||'%'")
    fun searchProviderFlow(query: String): Flowable<List<Providers>>

    @Transaction
    @Query("SELECT * FROM providers")
    fun getAllProvidersWithProducts(): Flowable<ProviderWithProducts>

    @Transaction
    @Query("SELECT * FROM providers")
    fun getAllProvidersWithPurchases(): Flowable<List<ProviderWithPurchases>>

    @Insert
    fun addProviders(vararg providers: Providers): Completable

    @Update
    fun updateProviders(vararg providers: Providers): Completable

    @Delete
    fun deleteProviders(vararg providers: Providers): Completable

    @Query("DELETE FROM providers")
    fun deleteAllProviders(): Completable

}