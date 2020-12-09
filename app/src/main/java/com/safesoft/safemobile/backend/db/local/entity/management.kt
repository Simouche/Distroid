package com.safesoft.safemobile.backend.db.local.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "inventories",
)
data class Inventories(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") val id: Long,
    @ColumnInfo(name = "NUMERO") val numero: Int,
    @ColumnInfo(name = "DATE") val date: Date,
    @ColumnInfo(name = "TIME") val time: String,
    @ColumnInfo(name = "LABEL") val label: String,
    @ColumnInfo(name = "PRODUCTS_COUNT", defaultValue = "0") val productsCount: Int = 0,
    @ColumnInfo(name = "HT_AMOUNT", defaultValue = "0.0") val htAmount: Double = 0.0,
    @ColumnInfo(name = "TVA_AMOUNT", defaultValue = "0.0") val tvaAmount: Double = 0.0,
    @ColumnInfo(name = "NEW_HT_AMOUNT", defaultValue = "0.0") val newHTAmount: Double = 0.0,
    @ColumnInfo(name = "NEW_TVA_AMOUNT", defaultValue = "0.0") val newTVAAmount: Double = 0.0,
    @ColumnInfo(name = "HT_DIFFERENCE", defaultValue = "0.0") val htDifference: Double = 0.0,
    @ColumnInfo(name = "TVA_DIFFERENCE", defaultValue = "0.0") val tvaDifference: Double = 0.0,
    @ColumnInfo(name = "STATUS") val status: String,
    @ColumnInfo(name = "WAREHOUSE") val warehouse: Int? = 0,
)

@Entity(
    tableName = "inventory_lines",
    foreignKeys = [
        ForeignKey(
            entity = Inventories::class,
            parentColumns = arrayOf("ID"),
            childColumns = arrayOf("INVENTORY"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Barcodes::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("BARCODE"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["BARCODE"], unique = false), Index(
        value = ["INVENTORY"],
        unique = false
    )]
)
data class InventoryLines(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") val id: Long,
    @ColumnInfo(name = "INVENTORY") val inventory: Long,
    @ColumnInfo(name = "BARCODE") val barcode: Long? = null,
    @ColumnInfo(name = "BATCH_CODE") val lot: String? = null,
    @ColumnInfo(name = "BUY_PRICE_HT") val buyPriceHT: Double = 0.0,
    @ColumnInfo(name = "TVA") val tva: Double = 0.0,
    @ColumnInfo(name = "QUANTITY") val quantity: Double = 0.0,
    @ColumnInfo(name = "TEMP_QUANTITY") val tempQuantity: Double = 0.0,
    @ColumnInfo(name = "NEW_QUANTITY") val newQuantity: Double = 0.0,
    @ColumnInfo(name = "HT_DIFFERENCE") val htDifference: Double = 0.0,
    @ColumnInfo(name = "TVA_DIFFERENCE") val tvaDifference: Double = 0.0
) {
    @Ignore
    var selectedProduct: AllAboutAProduct? = null
}

data class InventoryWithLines(
    @Embedded val inventory: Inventories,
    @Relation(
        parentColumn = "ID",
        entityColumn = "ID",
    ) val lines: List<InventoryLines>
)

