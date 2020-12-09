package com.safesoft.safemobile.backend.db.remote.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

interface BaseDao<T> {
    var tableName: String?
    var columns: List<String>
    var connection: Connection

    fun createInsertQuery(): String {
        val builder = StringBuffer()
        builder.append("INSERT INTO $tableName (")
        builder.append(columns.joinToString(","))
        builder.append(") VALUES(")
        for (i in columns.indices)
            if (i == columns.size - 1)
                builder.append("?);")
            else
                builder.append("?,")
        return builder.toString()
    }

    fun createUpdateQuery(where: String): String {
        val builder = StringBuffer()
        builder.append("UPDATE $tableName SET ")
        for ((i, item) in columns.withIndex())
            if (i == columns.size - 1)
                builder.append("$item = ? ")
            else builder.append("$item = ?, ")
        builder.append("WHERE $where;")
        return builder.toString()
    }

    fun createSelectQuery(specialSelect: String = "", where: String = ""): String {
        val builder = StringBuffer()
        builder.append("SELECT ")
        if (specialSelect.isNotEmpty())
            builder.append("$specialSelect ")
        else
            builder.append("* ")
        builder.append("FROM $tableName ")

        if (where.isNotEmpty())
            builder.append("WHERE $where")
        builder.append(";")
        return builder.toString()
    }

    fun prepareStatement(query: String): PreparedStatement {
        return connection.prepareStatement(query)
    }

    @ExperimentalStdlibApi
    fun prepareStatements(query: String, count: Int): List<PreparedStatement> {
        return buildList {
            for (i in 0 until count) connection.prepareStatement(query)
        }
    }

    fun bindParams(
        preparedStatement: PreparedStatement,
        values: Map<Int, Any>
    ): PreparedStatement {
        preparedStatement.clearParameters()
        values.forEach {
            when (it.value) {
                is Int -> preparedStatement.setInt(it.key, (it.value as Int))
                is String -> preparedStatement.setString(it.key, (it.value as String))
                is Double -> preparedStatement.setDouble(it.key, (it.value as Double))
                // TODO: 08/12/2020 completed the rest of the case
            }
        }
        return preparedStatement
    }

    fun executeUpdate(vararg preparedStatements: PreparedStatement): Int {
        connection.autoCommit = false
        var result = 0
        preparedStatements.forEach {
            result = it.executeUpdate()
        }
        connection.commit()
        return result
    }

    fun executeInsert(vararg preparedStatements: PreparedStatement): Int {
        connection.autoCommit = false
        var result = 0
        preparedStatements.forEach {
            result = it.executeUpdate()
        }
        return result
    }

    fun executeQuery(
        preparedStatement: PreparedStatement
    ): ResultSet {
        return preparedStatement.executeQuery()
    }

    fun executeQuery(query: String): ResultSet {
        return connection.createStatement().executeQuery(query)
    }

    fun insert(vararg objects: T)

    fun update(vararg objects: T)

    fun select(): List<T>


}