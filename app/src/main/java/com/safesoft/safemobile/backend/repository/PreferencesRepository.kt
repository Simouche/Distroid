package com.safesoft.safemobile.backend.repository

import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getAutomaticSync() = sharedPreferences.getBoolean("auto_sync", true)
}