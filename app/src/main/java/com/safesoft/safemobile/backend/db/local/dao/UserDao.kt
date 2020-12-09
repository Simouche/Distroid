package com.safesoft.safemobile.backend.db.local.dao

import androidx.room.*
import com.safesoft.safemobile.backend.db.local.entity.Users
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Flowable<List<Users>>

    @Query("SELECT * FROM users WHERE LOGGED = 1 LIMIT 1")
    fun checkLogged(): Single<Users>

    @Query("SELECT * FROM users WHERE USERNAME = :username")
    fun attemptLogin(username: String): Single<Users>

    @Query("UPDATE users SET LOGGED = 0")
    fun logOut(): Completable

    @Update
    fun update(users: Users): Completable

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(users: Users): Completable

    @Delete
    fun delete(users: Users): Completable
}