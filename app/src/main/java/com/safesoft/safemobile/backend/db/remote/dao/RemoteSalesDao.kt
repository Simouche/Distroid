package com.safesoft.safemobile.backend.db.remote.dao

import android.util.Log
import com.safesoft.safemobile.backend.db.local.entity.AllAboutASale
import com.safesoft.safemobile.backend.db.remote.RemoteDBRepository
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import com.safesoft.safemobile.backend.utils.isEmptyOrBlank
import io.reactivex.Completable
import io.reactivex.internal.operators.completable.CompletableFromCallable
import java.sql.Connection
import java.sql.ResultSet
import javax.inject.Inject

class RemoteSalesDao @Inject constructor(
    private val connector: RemoteDBRepository,
    override var preferencesRepository: PreferencesRepository
) : BaseDao<AllAboutASale> {
    override var tableName: String? = "BON1"
    val linesTableName: String = "BON2"

    override var selectQueryColumns: List<String> = listOf(
        "NUM_BON",
        "DATE_BON",
        "HEURE",
        "CODE_CLIENT",
        "NBR_P",
        "TOT_REMISE",
        "HT",
        "TVA",
        "TIMBRE",
        "REMISE",
        "VERSER",
        "MODE_RG",
        "CODE_CAISSE",
        "BLOCAGE",
        "MODE_TARIF",
        "TOT_QTE",
        "CODE_DEPOT",
        "AUTRE_INFO",
    )


    override var retrievalColumns: List<String> = selectQueryColumns

    override var insertColumns: List<String> = listOf(
        "NUM_BON",
        "DATE_BON",
        "HEURE",
        "CODE_CLIENT",
        "TIMBRE",
        "REMISE",
        "VERSER",
        "MODE_RG",
        "BLOCAGE",
        "MODE_TARIF",
        "CODE_DEPOT",
        "AUTRE_INFO",
        "UTILISATEUR",
    )

    val insertLineColumns = listOf(
        "NUM_BON",
        "CODE_BARRE",
        "PRODUIT",
        "QTE",
        "CODE_DEPOT",
        "REMISE",
        "PV_HT",
        "TVA",
    )

    override var connection: Connection? = connector.connection


    override val TAG: String
        get() = this::class.simpleName!!

    override fun checkConnection() {
        connection = connector.connection
    }

    override fun retrieve(resultSet: ResultSet): List<AllAboutASale> {
        TODO("Not yet implemented")
    }

    override fun insert(vararg items: AllAboutASale): Completable {
        return CompletableFromCallable {
            val warehouse =
                if (preferencesRepository.getWarehouseCode()?.isEmptyOrBlank() != false)
                    null
                else
                    preferencesRepository.getWarehouseCode()
            items.forEach {
                val code = generateCode("GEN_BON1_ID")
                val insertSaleQuery: String = createInsertQuery()
                val preparedStatement = prepareStatement(insertSaleQuery)
                val values = mutableMapOf<Int, Any?>(1 to code)
                values.putAll(it.toMap())
                values[10] = preferencesRepository.getTarifactionMode()
                values[11] = warehouse
                values[13] = user()
                val boundStatement = bindParams(preparedStatement, values)
                val rows: Int = executeInsert(boundStatement)
                Log.d(TAG, "insert: inserted a sale")
                if (rows != 0 && rows != -1)
                    it.saleLines.forEach { line ->
                        val insertLineQuery = createInsertQuery(linesTableName, insertLineColumns)
                        val preparedLineStatement = prepareStatement(insertLineQuery)
                        val lineValues = mutableMapOf<Int, Any?>(1 to code)
                        lineValues[5] = warehouse
                        lineValues.putAll(line.toMap())
                        val boundLineStatement = bindParams(preparedLineStatement, lineValues)
                        val linesRows: Int = executeInsert(boundLineStatement)

                        if (linesRows != 0 && linesRows != -1)
                            Log.d(TAG, "insert: inserted sale line")
                        else
                            Log.d(TAG, "insert: failed to insert sale line")
                    }
                Log.d(TAG, "insert: finished inserting a sale with its lines")
            }
            true
        }
    }

    override fun update(vararg items: AllAboutASale): Completable {
        TODO("Not yet implemented")
    }


}