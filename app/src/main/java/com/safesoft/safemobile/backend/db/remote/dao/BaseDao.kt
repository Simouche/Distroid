package com.safesoft.safemobile.backend.db.remote.dao

import android.util.Log
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

interface BaseDao<T> {
    var tableName: String?
    var selectQueryColumns: List<String>
    var retrievalColumns: List<String>
    var insertColumns: List<String>
    var connection: Connection?
    var preferencesRepository: PreferencesRepository

    val TAG: String

    fun createInsertQuery(): String {
        val builder = StringBuffer()
        builder.append("INSERT INTO $tableName (")
        builder.append(joinQueryColumns())
        builder.append(") VALUES(")
        for (i in insertColumns.indices)
            if (i == insertColumns.size - 1)
                builder.append("?);")
            else
                builder.append("?,")
        return builder.toString()
    }

    fun createUpdateQuery(where: String): String {
        val builder = StringBuffer()
        builder.append("UPDATE $tableName SET ")
        for ((i, item) in insertColumns.withIndex())
            if (i == insertColumns.size - 1)
                builder.append("$item = ? ")
            else builder.append("$item = ?, ")
        builder.append("WHERE $where;")
        return builder.toString()
    }

    private fun joinQueryColumns(): String {
        return selectQueryColumns.joinToString(", ")
    }

    fun createSelectQuery(specialSelect: String = "", where: String = ""): String {
        val builder = StringBuffer()
        builder.append("SELECT ")
        if (specialSelect.isNotEmpty())
            builder.append("$specialSelect ")
        else
            builder.append(joinQueryColumns())
        builder.append(" FROM $tableName ")

        if (where.isNotEmpty())
            builder.append("WHERE $where ")

        builder.append(";")
        Log.d(TAG, "created query: $builder")
        return builder.toString()
    }


    fun prepareStatement(query: String): PreparedStatement {
        Log.d(TAG, "preparing statement: $query")
        return connection!!.prepareStatement(query)
    }


    fun prepareStatements(query: String, count: Int): List<PreparedStatement> {
        val preparedStatements = mutableListOf<PreparedStatement>()
        for (i in 0 until count) preparedStatements.add(connection!!.prepareStatement(query))
        return preparedStatements

    }

    fun bindParams(preparedStatement: PreparedStatement, values: Map<Int, Any>): PreparedStatement {
        preparedStatement.clearParameters()
        values.forEach {
            when (it.value) {
                is Int -> preparedStatement.setInt(it.key, (it.value as Int))
                is String -> preparedStatement.setString(it.key, (it.value as String))
                is Double -> preparedStatement.setDouble(it.key, (it.value as Double))
                is Boolean -> preparedStatement.setBoolean(it.key, (it.value as Boolean))
                // TODO: 08/12/2020 completed the rest of the case
            }
        }
        return preparedStatement
    }

    fun executeUpdate(vararg preparedStatements: PreparedStatement): Int {
        connection!!.autoCommit = false
        var result = 0
        preparedStatements.forEach {
            result = it.executeUpdate()
        }
        connection!!.commit()
        return result
    }

    fun executeInsert(vararg preparedStatements: PreparedStatement): Int {
        connection!!.autoCommit = false
        var result = 0
        preparedStatements.forEach {
            result = it.executeUpdate()
        }
        return result
    }

    fun executeQuery(preparedStatement: PreparedStatement): ResultSet {
        return preparedStatement.executeQuery()
    }

    fun executeQuery(query: String): ResultSet {
        Log.d(TAG, "executing query: $query")
        return connection!!.createStatement().executeQuery(query)
    }

    fun checkConnection()

    fun retrieve(resultSet: ResultSet): List<T>

    fun insert(vararg items: T): Completable

    fun update(vararg items: T): Completable

    fun select(
        specialSelect: String = "",
        where: String = "",
        whereArgs: Map<Int, Any> = mapOf()
    ): Single<List<T>> {
        checkConnection()
        return Single.fromCallable {
            val query: String = createSelectQuery(specialSelect = specialSelect, where = where)
            val resultSet: ResultSet = if (whereArgs.isEmpty()) {
                executeQuery(query)
            } else {
                val preparedStatement = prepareStatement(query)
                val boundStatement = bindParams(preparedStatement, whereArgs)
                executeQuery(boundStatement)
            }

            retrieve(resultSet)
        }
    }


}