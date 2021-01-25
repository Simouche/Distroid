package com.safesoft.safemobile.backend.db.remote.dao

import android.util.Log
import com.safesoft.safemobile.backend.db.local.entity.InventoryWithLines
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import com.safesoft.safemobile.backend.utils.isEmptyOrBlank
import io.reactivex.Completable
import io.reactivex.internal.operators.completable.CompletableFromCallable
import java.sql.Connection
import java.sql.ResultSet
import javax.inject.Inject

class RemoteInventoryDao @Inject constructor(
    private val connector: RemoteDBRepository,
    override var preferencesRepository: PreferencesRepository
) : BaseDao<InventoryWithLines> {
    override val TAG: String = this::class.simpleName!!

    override var tableName: String? = "INV1"

    val linesTableName: String = "INV2"

    override var selectQueryColumns: List<String> = listOf(
        "NUM_INV",
        "DATE_INV",
        "HEURE",
        "LIBELLE",
        "NBR_PRODUIT",
        "MONTANT_HT",
        "MONTANT_TVA",
        "MONTANT_NEW_HT",
        "MONTANT_NEW_TVA",
        "ECART_HT",
        "ECART_TVA",
        "BLOCAGE",
        "UTILISATEUR",
        "CODE_DEPOT",
    )

    override var retrievalColumns: List<String> = selectQueryColumns

    override var insertColumns: List<String> = listOf(
        "NUM_INV",
        "DATE_INV",
        "HEURE",
        "LIBELLE",
        "NBR_PRODUIT",
        "MONTANT_HT",
        "MONTANT_TVA",
        "MONTANT_NEW_HT",
        "MONTANT_NEW_TVA",
        "ECART_HT",
        "ECART_TVA",
        "BLOCAGE",
        "UTILISATEUR",
        "CODE_DEPOT",
    )

    val insertLineColumns = listOf(
        "CODE_BARRE",
        "CODE_LOT",
        "NUM_INV",
        "PA_HT",
        "TVA",
        "QTE",
        "QTE_TMP",
        "QTE_NEW",
        "CODE_DEPOT",
    )


    override var connection: Connection? = connector.connection


    override fun checkConnection() {
        connection = connector.connection
    }

    override fun retrieve(resultSet: ResultSet): List<InventoryWithLines> {
        TODO("Not yet implemented")
    }

    override fun insert(vararg items: InventoryWithLines): Completable {
        return CompletableFromCallable {
            val warehouse =
                if (preferencesRepository.getWarehouseCode()?.isEmptyOrBlank() != false)
                    null
                else
                    preferencesRepository.getWarehouseCode()
            items.forEach {
                val code = generateCode("GEN_INV1_ID")
                val insertInventoryQuery: String = createInsertQuery()
                val preparedStatement = prepareStatement(insertInventoryQuery)
                val values = mutableMapOf<Int, Any?>(1 to code)
                values.putAll(it.toMap())
                values[13] = user()
                val boundStatement = bindParams(preparedStatement, values)
                val rows: Int = executeInsert(boundStatement)
                Log.d(TAG, "insert: inserted an inventory")
                if (rows != 0 && rows != -1)
                    it.lines.forEach { line ->
                        val insertLineQuery = createInsertQuery(linesTableName, insertLineColumns)
                        val preparedLineStatement = prepareStatement(insertLineQuery)
                        val lineValues = mutableMapOf<Int, Any?>(3 to code)
                        lineValues[9] = warehouse
                        lineValues.putAll(line.toMap())
                        val boundLineStatement = bindParams(preparedLineStatement, lineValues)
                        val linesRows: Int = executeInsert(boundLineStatement)

                        if (linesRows != 0 && linesRows != -1)
                            Log.d(TAG, "insert: inserted purchase line")
                        else
                            Log.d(TAG, "insert: failed to insert purchase line")
                    }
                Log.d(TAG, "insert: finished inserting a purchase with its lines")
            }
            true
        }

    }

    override fun update(vararg items: InventoryWithLines): Completable {
        TODO("Not yet implemented")
    }
}