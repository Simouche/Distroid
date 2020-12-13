package com.safesoft.safemobile.backend.db.remote.dao

import com.safesoft.safemobile.backend.db.local.entity.Products
import com.safesoft.safemobile.backend.db.local.entity.Providers
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import io.reactivex.Completable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteProductDao @Inject constructor(private val connector: RemoteDBRepository) :
    BaseDao<Products> {
    override var tableName: String? = "PRODUIT"

    override var selectQueryColumns: List<String> = listOf(
        "CODE_BARRE",
        "REF_PRODUIT",
        "COALESCE(PRODUIT,'vide') as PRODUIT",
        "PA_HT",
        "TVA",
        "PA_TTC",
        "PV1_HT",
        "PV1_TTC",
        "PV2_HT",
        "PV2_TTC",
        "PV3_HT",
        "PV3_TTC",
        "CODE_FRS",
        "PROMO",
        "MARQUE",
    )

    override var retrievalColumns: List<String> = listOf(
        "CODE_BARRE",
        "REF_PRODUIT",
        "PRODUIT",
        "PA_HT",
        "TVA",
        "PA_TTC",
        "PV1_HT",
        "PV1_TTC",
        "PV2_HT",
        "PV2_TTC",
        "PV3_HT",
        "PV3_TTC",
        "CODE_FRS",
        "PROMO",
        "MARQUE",
    )

    override var insertColumns: List<String> = listOf(
        "CODE_BARRE",
        "REF_PRODUIT",
        "PRODUIT",
        "PA_HT",
        "TVA",
        "PV1_HT",
        "PV2_HT",
        "PV3_HT",
        "CODE_FRS",
        "MARQUE",
    )

    override var connection: Connection? = connector.connection

    override val TAG: String
        get() = this::class.simpleName!!

    override fun checkConnection() {
        connection = connector.connection
    }

    override fun retrieve(resultSet: ResultSet): List<Products> {
        val retrievedData = mutableListOf<Products>()
        while (resultSet.next()) {
            val product = Products(
                id = 0,
                reference = resultSet.getString(retrievalColumns[1]),
                designation = resultSet.getString(retrievalColumns[2]),
                purchasePriceHT = resultSet.getDouble(retrievalColumns[3]),
                tva = resultSet.getDouble(retrievalColumns[4]),
                purchasePriceTTC = resultSet.getDouble(retrievalColumns[5]),
                sellPriceDetailHT = resultSet.getDouble(retrievalColumns[6]),
                sellPriceDetailTTC = resultSet.getDouble(retrievalColumns[7]),
                sellPriceWholeHT = resultSet.getDouble(retrievalColumns[8]),
                sellPriceHalfWholeTTC = resultSet.getDouble(retrievalColumns[9]),
                sellPriceHalfWholeHT = resultSet.getDouble(retrievalColumns[10]),
                sellPriceWholeTTC = resultSet.getDouble(retrievalColumns[11]),
                promotion = resultSet.getDouble(retrievalColumns[13]),
            )
            product.barcode = resultSet.getString(retrievalColumns[0])
            retrievedData.add(product)
        }
        return retrievedData
    }

    override fun insert(vararg items: Products): Completable {
        return Completable.fromCallable {
            val query: String = createInsertQuery()
            val statements: List<PreparedStatement> = prepareStatements(query, items.size)
            val boundStatements = mutableListOf<PreparedStatement>()
            for ((index, product) in items.withIndex())
                boundStatements.add(bindParams(statements[index], product.toMap()))
            val rows: Int = executeInsert(*boundStatements.toTypedArray())
            rows
        }
    }

    override fun update(vararg items: Products): Completable {
        TODO("Not yet implemented")
    }
}