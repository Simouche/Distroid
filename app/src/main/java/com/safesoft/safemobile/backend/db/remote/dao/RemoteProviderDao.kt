package com.safesoft.safemobile.backend.db.remote.dao

import com.safesoft.safemobile.backend.db.local.entity.Providers
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.inject.Inject

class RemoteProviderDao @Inject constructor(connector: RemoteDBRepository) : BaseDao<Providers> {
    override var tableName: String? = "FOURNIS"
    override var columns: List<String> = listOf(
        "CODE_FRS",
        "FOURNIS",
        "TEL",
        "ADRESSE",
        "COMMUNE",
        "WILAYA",
        "COALESCE(FOURNIS.ACHATS, 0) AS ACHATS",
        "COALESCE(FOURNIS.VERSER, 0) AS VERSER",
        "COALESCE(FOURNIS.SOLDE, 0) AS SOLDE",
    )
    override var connection: Connection = connector.connection!!

    @ExperimentalStdlibApi
    override fun insert(vararg providers: Providers) {
        val query: String = createInsertQuery()
        val statements: List<PreparedStatement> = prepareStatements(query, providers.size)
        val boundStatements = mutableListOf<PreparedStatement>()
        for ((index, provider) in providers.withIndex())
            boundStatements.add(bindParams(statements[index], provider.toMap()))
        val rows: Int = executeInsert(*boundStatements.toTypedArray())
    }

    override fun update(vararg providers: Providers) {
        TODO("Not yet implemented")
    }

    @ExperimentalStdlibApi
    override fun select(): List<Providers> {
        val query: String = createSelectQuery()
        val resultSet: ResultSet = executeQuery(query)
        return buildList {
            while (resultSet.next()) Providers(
                id = 0,
                code = resultSet.getString(columns[0]),
                name = resultSet.getString(columns[1]),
                phones = resultSet.getString(columns[2]),
                address = resultSet.getString(columns[3]),
                commune = resultSet.getString(columns[4]),
                purchases = resultSet.getDouble(columns[5]),
                payments = resultSet.getDouble(columns[6]),
                sold = resultSet.getDouble(columns[7]),
            )
        }
    }


}