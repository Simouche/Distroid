package com.safesoft.safemobile.backend.db.remote.dao

import com.safesoft.safemobile.backend.db.local.entity.Barcodes
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteBarCodeDao @Inject constructor(private val connector: RemoteDBRepository) :
    BaseDao<Barcodes> {


    override var tableName: String? = "CODEBARRE"

    override var selectQueryColumns: List<String> = listOf("CODE_BARRE", "CODE_BARRE_SYN")

    override var retrievalColumns: List<String> = listOf("CODE_BARRE", "CODE_BARRE_SYN")

    override var insertColumns: List<String> = listOf("CODE_BARRE", "CODE_BARRE_SYN")

    override var connection: Connection? = connector.connection

    override val TAG: String
        get() = this::class.simpleName!!

    override fun checkConnection() {
        connection = connector.connection
    }

    override fun retrieve(resultSet: ResultSet): List<Barcodes> {
        val retrievedData = mutableListOf<Barcodes>()
        while (resultSet.next())
            retrievedData.add(
                Barcodes(
                    id = 0L,
                    code = resultSet.getString(retrievalColumns[1]),
                    product = 0L
                )
            )
        return retrievedData
    }

    override fun insert(vararg items: Barcodes): Completable {
        return Completable.fromCallable {
            val query: String = createInsertQuery()
            val statements: List<PreparedStatement> = prepareStatements(query, items.size)
            val boundStatements = mutableListOf<PreparedStatement>()
            for ((index, provider) in items.withIndex())
                boundStatements.add(bindParams(statements[index], mapOf()))
            val rows: Int = executeInsert(*boundStatements.toTypedArray())
            rows
        }
    }

    override fun update(vararg items: Barcodes): Completable {
        TODO("Not yet implemented")
    }

}