package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.db.local.dao.HomeDao
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeDao: HomeDao) {

    fun getCounts() = homeDao.getCounts()
}