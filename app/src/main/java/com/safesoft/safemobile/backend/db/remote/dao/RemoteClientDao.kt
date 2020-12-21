package com.safesoft.safemobile.backend.db.remote.dao

import com.safesoft.safemobile.backend.db.local.entity.Clients
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteClientDao @Inject constructor(private val connector: RemoteDBRepository,
                                          override var preferencesRepository: PreferencesRepository
) : BaseDao<Clients> {
    override var tableName: String? = "CLIENTS"

    override var selectQueryColumns: List<String> = listOf(
        "CODE_CLIENT",
        "CLIENT",
        "TEL",
        "ADRESSE",
        "COMMUNE",
        "COALESCE(ACHATS, 0) AS ACHATS",
        "COALESCE(VERSER, 0) AS VERSER",
        "COALESCE(SOLDE, 0) AS SOLDE",
    )
    override var retrievalColumns: List<String> = listOf(
        "CODE_CLIENT",
        "CLIENT",
        "TEL",
        "ADRESSE",
        "COMMUNE",
        "ACHATS",
        "VERSER",
        "SOLDE",
    )
    override var insertColumns: List<String> = listOf(
        "CODE_CLIENT",
        "CLIENT",
        "TEL",
        "ADRESSE",
        "COMMUNE",
    )
    override var connection: Connection? = connector.connection

    override val TAG: String
        get() = this::class.simpleName!!

    override fun retrieve(resultSet: ResultSet): List<Clients> {
        val retrievedData = mutableListOf<Clients>()
        while (resultSet.next())
            retrievedData.add(
                Clients(
                    id = 0,
                    code = resultSet.getString(retrievalColumns[0]),
                    name = resultSet.getString(retrievalColumns[1]),
                    phones = resultSet.getString(retrievalColumns[2]),
                    address = resultSet.getString(retrievalColumns[3]),
                    commune = resultSet.getString(retrievalColumns[4]),
                    sales = resultSet.getDouble(retrievalColumns[5]),
                    payments = resultSet.getDouble(retrievalColumns[6]),
                    sold = resultSet.getDouble(retrievalColumns[7]),
                )
            )
        return retrievedData
    }

    override fun insert(vararg items: Clients): Completable {
        return Completable.fromCallable {
            val query: String = createInsertQuery()
            val statements: List<PreparedStatement> = prepareStatements(query, items.size)
            val boundStatements = mutableListOf<PreparedStatement>()
            for ((index, client) in items.withIndex())
                boundStatements.add(bindParams(statements[index], client.toMap()))
            val rows: Int = executeInsert(*boundStatements.toTypedArray())
            rows
        }
    }

    override fun update(vararg items: Clients): Completable {
        TODO("Not yet implemented")
    }

    override fun checkConnection() {
        connection = connector.connection
    }
}