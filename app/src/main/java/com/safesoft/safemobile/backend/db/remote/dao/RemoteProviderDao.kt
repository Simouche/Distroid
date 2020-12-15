package com.safesoft.safemobile.backend.db.remote.dao

import com.safesoft.safemobile.backend.db.local.entity.Providers
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
class RemoteProviderDao @Inject constructor(
    private val connector: RemoteDBRepository,
    override var preferencesRepository: PreferencesRepository
) :
    BaseDao<Providers> {
    override var tableName: String? = "FOURNIS"
    override var selectQueryColumns: List<String> = listOf(
        "CODE_FRS",
        "FOURNIS",
        "TEL",
        "ADRESSE",
        "COMMUNE",
        "COALESCE(FOURNIS.ACHATS, 0) AS ACHATS",
        "COALESCE(FOURNIS.VERSER, 0) AS VERSER",
        "COALESCE(FOURNIS.SOLDE, 0) AS SOLDE",
    )
    override var retrievalColumns: List<String> = listOf(
        "CODE_FRS",
        "FOURNIS",
        "TEL",
        "ADRESSE",
        "COMMUNE",
        "ACHATS",
        "VERSER",
        "SOLDE",
    )
    override var insertColumns: List<String> = listOf(
        "CODE_FRS",
        "FOURNIS",
        "TEL",
        "ADRESSE",
        "COMMUNE",
    )
    override var connection: Connection? = connector.connection

    override val TAG: String
        get() = this::class.simpleName!!


    override fun insert(vararg items: Providers): Completable {
        return Completable.fromCallable {
            val query: String = createInsertQuery()
            val statements: List<PreparedStatement> = prepareStatements(query, items.size)
            val boundStatements = mutableListOf<PreparedStatement>()
            for ((index, provider) in items.withIndex())
                boundStatements.add(bindParams(statements[index], provider.toMap()))
            val rows: Int = executeInsert(*boundStatements.toTypedArray())
            rows
        }
    }

    override fun update(vararg items: Providers): Completable {
        TODO("Not yet implemented")
    }

    override fun retrieve(resultSet: ResultSet): List<Providers> {
        val retrievedData = mutableListOf<Providers>()
        while (resultSet.next())
            retrievedData.add(
                Providers(
                    id = 0,
                    code = resultSet.getString(retrievalColumns[0]),
                    name = resultSet.getString(retrievalColumns[1]),
                    phones = resultSet.getString(retrievalColumns[2]),
                    address = resultSet.getString(retrievalColumns[3]),
                    commune = resultSet.getString(retrievalColumns[4]),
                    purchases = resultSet.getDouble(retrievalColumns[5]),
                    payments = resultSet.getDouble(retrievalColumns[6]),
                    sold = resultSet.getDouble(retrievalColumns[7]),
                )
            )
        return retrievedData
    }

    override fun checkConnection() {
        connection = connector.connection
    }


}