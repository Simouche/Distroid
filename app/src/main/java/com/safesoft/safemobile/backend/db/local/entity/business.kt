package com.safesoft.safemobile.backend.db.local.entity

import androidx.room.*
import com.safesoft.safemobile.backend.utils.formatted
import java.util.*


@Entity(
    tableName = "purchases",
    foreignKeys = [ForeignKey(
        entity = Providers::class,
        parentColumns = arrayOf("PROVIDER_ID"),
        childColumns = arrayOf("PROVIDER"),
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )], indices = [Index(value = ["PROVIDER"], unique = false)]
)
data class Purchases(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "PURCHASE_NUMBER") val purchaseNumber: String?,
    @ColumnInfo(name = "INVOICE_NUMBER") val invoiceNumber: String?,
    @ColumnInfo(name = "DATE") val date: Date?,
    @ColumnInfo(name = "PROVIDER") val provider: Long?,
    @ColumnInfo(name = "PRODUCTS_COUNT") val productsCount: Int?,
    @ColumnInfo(name = "TOTAL_HT") val totalHT: Double?,
    @ColumnInfo(name = "TOTAL_TTC") val totalTTC: Double?,
    @ColumnInfo(name = "TVA") val tva: Double?,
    @ColumnInfo(name = "STAMP") val stamp: Double?,
    @ColumnInfo(name = "DISCOUNT") val discount: Double?,
    @ColumnInfo(name = "REGLEMENT", defaultValue = "C") val reglement: String = "C",
    @ColumnInfo(name = "TOTAL_QUANTITY") val totalQuantity: Double?,
    @ColumnInfo(name = "NOTE") val note: String?,
    @ColumnInfo(name = "DONE", defaultValue = "0") val done: Boolean,
    @ColumnInfo(name = "SYNC", defaultValue = "0") val sync: Boolean,
    @ColumnInfo(name = "PAYMENT", defaultValue = "0") val payment: Double = 0.0
) {
    @Ignore
    lateinit var stringDate: String
}

/**
 * total = quantity * productPriceDiscounted
 */
@Entity(
    tableName = "purchase_lines",
    foreignKeys = [ForeignKey(
        entity = Purchases::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("PURCHASE"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["PURCHASE"], unique = false), Index(
        value = ["PRODUCT"],
        unique = false
    )]
)
data class PurchaseLines(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "PRODUCT") val product: Long,
    @ColumnInfo(name = "QUANTITY") val quantity: Double?,
    @ColumnInfo(name = "BUY_PRICE_HT") val totalBuyPriceHT: Double = 0.0,
    @ColumnInfo(name = "DISCOUNT") val discount: Double = 0.0,
    @ColumnInfo(name = "BUY_PRICE_TTC") val totalBuyPriceTTC: Double = 0.0,
    @ColumnInfo(name = "TVA") val tva: Double = 0.0,
    @ColumnInfo(name = "NOTE") val note: String?,
    @ColumnInfo(name = "PURCHASE") val purchase: Long,
) {
    @Ignore
    var selectedProduct: AllAboutAProduct? = null

    fun toMap(): Map<Int, Any?> {
        return mapOf(
            2 to this.selectedProduct?.bardcodes?.get(0)?.code,
            3 to this.selectedProduct?.product?.designation,
            4 to this.quantity,
            6 to this.discount,
            7 to this.selectedProduct?.product?.purchasePriceHT,
            8 to this.selectedProduct?.product?.tva,
        )
    }

}

data class PurchaseWithLines(
    @Embedded val purchase: Purchases,
    @Relation(
        parentColumn = "id",
        entityColumn = "PURCHASE"
    ) val lines: List<PurchaseLines>
)

data class AllAboutAPurchase(
    @Embedded val purchase: Purchases,
    @Relation(
        parentColumn = "id",
        entityColumn = "PURCHASE"
    ) val purchaseLines: List<PurchaseLines>,
    @Relation(
        parentColumn = "PROVIDER",
        entityColumn = "PROVIDER_ID"
    ) val provider: Providers
) {


    fun toMap(): Map<Int, Any?> {
        return mapOf(
            2 to this.purchase.date,
            3 to this.purchase.date?.formatted()?.split(" ")?.get(1),
            4 to this.provider.code,
            5 to this.purchase.stamp,
            6 to this.purchase.discount,
            7 to this.purchase.payment,
            8 to if (this.purchase.reglement == "C") "ESPECE" else "A TERME",
            9 to (if (this.purchase.done) "F" else "O"),
            12 to this.purchase.note
        )
    }
}

@Entity(
    tableName = "sales",
    foreignKeys = [ForeignKey(
        entity = Clients::class,
        parentColumns = arrayOf("CLIENT_ID"),
        childColumns = arrayOf("CLIENT"),
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )], indices = [Index(value = ["CLIENT"], unique = false)]
)
data class Sales(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "SALE_NUMBER") val purchaseNumber: String?,
    @ColumnInfo(name = "INVOICE_NUMBER") val invoiceNumber: String?,
    @ColumnInfo(name = "DATE") val date: Date?,
    @ColumnInfo(name = "CLIENT") val client: Long?,
    @ColumnInfo(name = "PRODUCTS_COUNT") val productsCount: Int?,
    @ColumnInfo(name = "TOTAL_HT") val totalHT: Double?,
    @ColumnInfo(name = "TOTAL_TTC") val totalTTC: Double?,
    @ColumnInfo(name = "TVA") val tva: Double?,
    @ColumnInfo(name = "STAMP") val stamp: Double?,
    @ColumnInfo(name = "DISCOUNT") val discount: Double?,
    @ColumnInfo(name = "REGLEMENT", defaultValue = "E") val reglement: String = "E",
    @ColumnInfo(name = "TOTAL_QUANTITY") val totalQuantity: Double?,
    @ColumnInfo(name = "NOTE") val note: String?,
    @ColumnInfo(name = "DONE", defaultValue = "0") val done: Boolean,
    @ColumnInfo(name = "SYNC", defaultValue = "0") val sync: Boolean,
    @ColumnInfo(name = "PAYMENT", defaultValue = "0") val payment: Double = 0.0
)


@Entity(
    tableName = "sale_lines",
    foreignKeys = [ForeignKey(
        entity = Sales::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("SALE"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["SALE"], unique = false), Index(
        value = ["PRODUCT"],
        unique = false
    )]
)
data class SaleLines(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "PRODUCT") val product: Long,
    @ColumnInfo(name = "QUANTITY") val quantity: Double?,
    @ColumnInfo(name = "SELL_PRICE_HT") val totalSellPriceHT: Double = 0.0,
    @ColumnInfo(name = "DISCOUNT") val discount: Double = 0.0,
    @ColumnInfo(name = "TVA") val tva: Double?,
    @ColumnInfo(name = "SELL_PRICE_TTC") val totalSellPriceTTC: Double = 0.0,
    @ColumnInfo(name = "NOTE") val note: String?,
    @ColumnInfo(name = "SALE") val sale: Long,
) {
    @Ignore
    var selectedProduct: AllAboutAProduct? = null

    fun toMap(): Map<Int, Any?> {
        return mapOf(
            2 to this.selectedProduct?.bardcodes?.get(0)?.code,
            3 to this.selectedProduct?.product?.designation,
            4 to this.quantity,
            6 to this.discount,
            7 to this.selectedProduct?.product?.sellPriceDetailHT,
            8 to this.selectedProduct?.product?.tva,
        )
    }

}

data class AllAboutASale(
    @Embedded val sale: Sales,
    @Relation(
        parentColumn = "id",
        entityColumn = "SALE"
    ) val saleLines: List<SaleLines>,
    @Relation(
        parentColumn = "CLIENT",
        entityColumn = "CLIENT_ID"
    ) val client: Clients
) {
    fun toMap(): Map<Int, Any?> {
        return mapOf(
            2 to this.sale.date,
            3 to this.sale.date?.formatted()?.split(" ")?.get(1),
            4 to this.client.code,
            5 to this.sale.stamp,
            6 to this.sale.discount,
            7 to this.sale.payment,
            8 to if (this.sale.reglement == "C") "ESPECE" else "A TERME",
            9 to (if (this.sale.done) "F" else "O"),
            12 to this.sale.note
        )
    }
}


@Entity(
    tableName = "client_payments",
    foreignKeys = [ForeignKey(
        entity = Clients::class,
        parentColumns = arrayOf("CLIENT_ID"),
        childColumns = arrayOf("CLIENT"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["CLIENT"], unique = false)]
)
data class ClientPayments(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") val id: Long,
    @ColumnInfo(name = "AMOUNT") val amount: Double,
    @ColumnInfo(name = "CLIENT") val client: Long,
    @ColumnInfo(name = "DATE") val date: Date,
    @ColumnInfo(name = "NOTE") val note: String
)

@Entity(
    tableName = "provider_payments",
    foreignKeys = [ForeignKey(
        entity = Providers::class,
        parentColumns = arrayOf("PROVIDER_ID"),
        childColumns = arrayOf("PROVIDER"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["PROVIDER"], unique = false)]
)
data class ProviderPayments(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "ID") val id: Long,
    @ColumnInfo(name = "AMOUNT") val amount: Double,
    @ColumnInfo(name = "PROVIDER") val provider: Long,
    @ColumnInfo(name = "DATE") val date: Date,
    @ColumnInfo(name = "NOTE") val note: String
)


// TODO: 9/20/2020 make the fucking sales!


