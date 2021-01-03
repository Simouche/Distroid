package com.safesoft.safemobile.backend.db.remote

import android.util.Log
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDBRepository @Inject constructor(private val preferencesRepository: PreferencesRepository) {
    private val driverClassName = "org.firebirdsql.jdbc.FBDriver"

    init {
        val checker = checkConnection().subscribeOn(Schedulers.io())
            .subscribe(
                { Log.i(TAG, "DB connected successfully") },
                {
                    it.printStackTrace()
                }
            )
    }

    val TAG = this::class.simpleName

    var connection: Connection? = null

    private fun connect() {
        System.setProperty("FBAdbLog", "true")
        DriverManager.setLoginTimeout(5)
        Class.forName(driverClassName)
        val configs = loadConfigs()
        val url =
            "jdbc:firebirdsql://${configs["server"]}/${configs["path"]}?encoding=ISO8859_1"
        Log.d(TAG, "Connection URL: $url")
        connection = DriverManager.getConnection(url, configs["username"], configs["password"])
    }

    fun checkConnection(): Completable {
        return Completable.fromCallable {
            return@fromCallable connect()
        }
    }

    private fun loadConfigs(): Map<String, String> {
        val server: String = preferencesRepository.getLocalServerIp()!!
        val path: String = preferencesRepository.getDBPath()
        val dbUser = preferencesRepository.getDBUsername()!!
        val dbPassword = preferencesRepository.getDBPassword()!!
        return mapOf(
            "path" to path,
            "server" to server,
            "username" to dbUser,
            "password" to dbPassword,
        )
    }


}