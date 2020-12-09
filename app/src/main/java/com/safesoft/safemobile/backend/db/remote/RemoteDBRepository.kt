package com.safesoft.safemobile.backend.db.remote

import android.util.Log
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.inject.Inject

class RemoteDBRepository @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    val TAG = this::class.simpleName

    var connection: Connection? = null
        get() {
            if (field == null)
                connect()
            return field
        }

    fun connect(): Boolean {
        return try {
            System.setProperty("FBAdbLog", "true")
            DriverManager.setLoginTimeout(5)
            Class.forName("org.firebirdsql.jdbc.FBDriver")
            val configs = loadConfigs()
            val url =
                "jdbc:firebirdsql://${configs["server"]}/${configs["path"]}?encoding=ISO8859_1"
            Log.d(TAG, "Connection URL: $url")
            connection = DriverManager.getConnection(url, "SYSDBA", "masterkey")
            true
        } catch (error: SQLException) {
            error.printStackTrace()
            false
        }
    }

    private fun loadConfigs(): Map<String, String> {
        val server: String = preferencesRepository.getLocalServerIp()!!
        val path: String = preferencesRepository.getDBPath()
        return mapOf("path" to path, "server" to server)
    }


}