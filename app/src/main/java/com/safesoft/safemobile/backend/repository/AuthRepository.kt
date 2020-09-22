package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.dao.UserDao
import com.safesoft.safemobile.backend.db.entity.Users
import javax.inject.Inject


class AuthRepository @Inject constructor(val userDao: UserDao) {
    fun getAll() = userDao.getAll()

    fun checkLogged() = userDao.checkLogged()

    fun attemptLogin(username: String) = userDao.attemptLogin(username = username)

    fun update(users: Users) = userDao.update(users = users)

    fun insert(users: Users) = userDao.insert(users)

    fun delete(users: Users) = userDao.delete(users)

    fun logOut() = userDao.logOut()

}