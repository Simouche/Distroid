package com.safesoft.safemobile.backend.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.safesoft.safemobile.backend.db.entity.Clients
import com.safesoft.safemobile.backend.db.entity.Providers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface ClientsDao {
    @Query("SELECT * FROM clients")
    fun getAllClients(): DataSource.Factory<Int, Clients>

    @Query("SELECT * FROM clients where CLIENT_ID=:id")
    fun getClientById(id: Long): Single<Clients>

    @Query("SELECT * FROM clients WHERE CODE LIKE '%'||:query||'%' OR NAME LIKE '%'||:query||'%' OR PHONES LIKE '%'||:query||'%'")
    fun searchClient(query: String): DataSource.Factory<Int, Clients>

    @Query("SELECT * FROM clients WHERE CODE LIKE '%'||:query||'%' OR NAME LIKE '%'||:query||'%' OR PHONES LIKE '%'||:query||'%'")
    fun searchClientFlow(query: String): Flowable<List<Clients>>

    @Insert
    fun addClients(vararg clients: Clients): Completable

    @Update
    fun updateClients(vararg clients: Clients): Completable

    @Delete
    fun deleteClients(vararg clients: Clients): Completable

}