package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.local.SafeDatabase
import com.safesoft.safemobile.backend.db.local.dao.UserDao
import com.safesoft.safemobile.backend.db.local.entity.Users
import io.reactivex.Completable
import javax.inject.Inject

class SyncRepository @Inject constructor(
    private val safeDataBase: SafeDatabase,
    private val userDao: UserDao
) {

    fun clearAllTables(): Completable {
        return userDao.attemptLogin(username = "ADMIN")
            .flatMapCompletable {
                val user: Users = it
                safeDataBase.clearAllTables()
                userDao.insert(user)
            }
    }
}