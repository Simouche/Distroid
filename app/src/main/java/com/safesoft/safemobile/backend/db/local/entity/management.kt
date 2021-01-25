package com.safesoft.safemobile.backend.db.local.entity

import androidx.room.*
import com.safesoft.safemobile.backend.utils.formatted
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
    @ColumnInfo(name = "SYNC", defaultValue = "0") val sync: Boolean,
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

    fun toMap(): Map<Int, Any?> {
        return mapOf(
            1 to this.selectedProduct?.bardcodes?.get(0)?.code,
            2 to this.lot,
            4 to this.buyPriceHT,
            5 to this.tva,
            6 to this.quantity,
            7 to this.tempQuantity,
            8 to this.newQuantity,
        )
    }
}

data class InventoryWithLines(
    @Embedded val inventory: Inventories,
    @Relation(
        parentColumn = "ID",
        entityColumn = "ID",
    ) val lines: List<InventoryLines>
) {

    fun toMap(): Map<Int, Any?> {
        return mapOf(
            2 to this.inventory.date,
            3 to this.inventory.date.formatted().split(" ")[1],
            4 to this.inventory.label,
            5 to this.inventory.productsCount,
            6 to this.inventory.htAmount,
            7 to this.inventory.tvaAmount,
            8 to this.inventory.newHTAmount,
            9 to this.inventory.newTVAAmount,
            10 to this.inventory.htDifference,
            11 to this.inventory.tvaDifference,
            12 to this.inventory.status,
            14 to if (this.inventory.warehouse == 0) null else this.inventory.warehouse,
        )
    }
}

