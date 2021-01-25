package com.safesoft.safemobile.backend.db.remote.dao

import android.util.Log
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*

interface BaseDao<T> {
    var tableName: String?
    var selectQueryColumns: List<String>
    var retrievalColumns: List<String>
    var insertColumns: List<String>
    var connection: Connection?
    var preferencesRepository: PreferencesRepository

    fun user(): String = "TERMINAL MOBILE"

    val TAG: String

    fun createInsertQuery(tableName: String? = null, insertColumns: List<String>? = null): String {
        val builder = StringBuffer()
        builder.append("INSERT INTO ${tableName ?: this.tableName} (")
        builder.append(joinInsertColumns(insertColumns))
        builder.append(") VALUES (")
        for (i in (insertColumns ?: this.insertColumns).indices)
            if (i == (insertColumns ?: this.insertColumns).size - 1)
                builder.append("?);")
            else
                builder.append("?,")
        Log.d(TAG, "createInsertQuery: $builder")
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

    private fun joinInsertColumns(insertColumns: List<String>? = null): String {
        return (insertColumns ?: this.insertColumns).joinToString(", ")
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
        checkConnection()
        return connection!!.prepareStatement(query)
    }


    fun prepareStatements(query: String, count: Int): List<PreparedStatement> {
        val preparedStatements = mutableListOf<PreparedStatement>()
        checkConnection()
        for (i in 0 until count) preparedStatements.add(connection!!.prepareStatement(query))
        Log.d(TAG, "prepareStatements: prepared ${preparedStatements.size} statements")
        return preparedStatements

    }

    fun bindParams(
        preparedStatement: PreparedStatement,
        values: Map<Int, Any?>
    ): PreparedStatement {
        Log.d(TAG, "bindParams: preparing to bind a prepared statement")
        preparedStatement.clearParameters()
        values.forEach {
            when (it.value) {
                is Int -> preparedStatement.setInt(it.key, (it.value as Int))
                is String -> preparedStatement.setString(it.key, (it.value as String))
                is Double -> preparedStatement.setDouble(it.key, (it.value as Double))
                is Boolean -> preparedStatement.setBoolean(it.key, (it.value as Boolean))
                is Date -> preparedStatement.setDate(it.key, java.sql.Date((it.value as Date).time))
                null -> preparedStatement.setObject(it.key, null)
                // TODO: 08/12/2020 complete the rest of the case
            }
            Log.d(TAG, "bindParams: the entry ${it.key}:${it.value} is bound ")
        }
        Log.d(TAG, "bindParams: bound a statement.")
        return preparedStatement
    }

    fun executeUpdate(vararg preparedStatements: PreparedStatement): Int {
        checkConnection()
        connection!!.autoCommit = false
        var result = 0
        preparedStatements.forEach {
            result = it.executeUpdate()
        }
        connection!!.commit()
        return result
    }

    fun executeInsert(vararg preparedStatements: PreparedStatement): Int {
        checkConnection()
        Log.d(TAG, "executeInsert: starting execution")
        connection!!.autoCommit = false
        var result = 0
        preparedStatements.forEach {
            try {
                val rows = it.executeUpdate()
                result += rows
                Log.d(TAG, "executeInsert: inserted $rows rows")
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
        connection!!.commit()
        Log.d(TAG, "executeInsert: exiting after $result insertions")
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


    fun generateCode(table: String): String {
        val resultSet = executeQuery("SELECT GEN_ID($table,1) FROM RDB\$DATABASE")
        var code = ""
        while (resultSet.next())
            code = resultSet.getString("GEN_ID")
        val length = code.length
        for (i in 0 until (6 - length))
            code = "0$code"
        return code
    }

}