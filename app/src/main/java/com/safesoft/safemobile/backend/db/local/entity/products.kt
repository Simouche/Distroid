package com.safesoft.safemobile.backend.db.local.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.safesoft.safemobile.backend.utils.calculatePercentageValue
import java.util.*

@Entity(
    tableName = "barcodes",
    indices = [Index(value = ["CODE"], unique = true), Index(value = ["PRODUCT"], unique = false)],
    foreignKeys = [ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Barcodes(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "CODE") val code: String,
    @ColumnInfo(name = "PRODUCT") val product: Long
) {
    @Ignore
    lateinit var mainBarCode: String

    fun toMap(): Map<Int, Any> {
        return mapOf(
            1 to mainBarCode,
            2 to code
        )
    }
}


@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Brands::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("BRAND"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["BRAND"], unique = false)]
)
data class Products(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "PRODUCT_ID") val id: Long,
    @SerializedName("refProduit") @ColumnInfo(name = "REFERENCE") val reference: String,
    @SerializedName("produit") @ColumnInfo(name = "DESIGNATION") val designation: String,
    @SerializedName("stock") @ColumnInfo(
        name = "QUANTITY",
        defaultValue = "0"
    ) val quantity: Double? = null,
    @SerializedName("paHT") @ColumnInfo(
        name = "PURCHASE_PRICE_HT",
        defaultValue = "0"
    ) val purchasePriceHT: Double? = null,
    @SerializedName("tva") @ColumnInfo(name = "TVA", defaultValue = "19") val tva: Double? = null,
    @SerializedName("paTTC") @ColumnInfo(
        name = "PURCHASE_PRICE_TTC",
        defaultValue = "0"
    ) val purchasePriceTTC: Double? = null,
    @ColumnInfo(
        name = "STEADY_PURCHASE_PRICE_HT",
        defaultValue = "0"
    ) val steadyPurchasePriceHT: Double? = null,
    @ColumnInfo(
        name = "STEADY_PURCHASE_PRICE_TTC",
        defaultValue = "0"
    ) val steadyPurchasePriceTTC: Double? = null,
    @ColumnInfo(name = "MARGE") val marge: Double? = null,
    @SerializedName("pv1HT") @ColumnInfo(
        name = "SELL_PRICE_DETAIL_HT",
        defaultValue = "0"
    ) val sellPriceDetailHT: Double = 0.0,
    @SerializedName("pv2HT") @ColumnInfo(
        name = "SELL_PRICE_WHOLE_HT",
        defaultValue = "0"
    ) val sellPriceWholeHT: Double = 0.0,
    @SerializedName("pv3HT") @ColumnInfo(
        name = "SELL_PRICE_HAF_WHOLE_HT",
        defaultValue = "0"
    ) val sellPriceHalfWholeHT: Double = 0.0,
    @SerializedName("pv1TTC") @ColumnInfo(
        name = "SELL_PRICE_DETAIL_TTC",
        defaultValue = "0"
    ) val sellPriceDetailTTC: Double = 0.0,
    @SerializedName("pv2TTC") @ColumnInfo(
        name = "SELL_PRICE_WHOLE_TTC",
        defaultValue = "0"
    ) val sellPriceWholeTTC: Double = 0.0,
    @SerializedName("pv3TTC") @ColumnInfo(
        name = "SELL_PRICE_HAF_WHOLE_TTC",
        defaultValue = "0"
    ) val sellPriceHalfWholeTTC: Double = 0.0,
    @ColumnInfo(name = "PHOTO") val photo: String? = null,
    @ColumnInfo(name = "QUANTITY_PER_PACKAGE") val quantityPerPackage: Long? = null,
    @SerializedName("promo") @ColumnInfo(
        name = "PROMO",
        defaultValue = "0"
    ) val promotion: Double = 0.0,
    @ColumnInfo(name = "PROMO_THRESHOLD", defaultValue = "1") val promotionThreshold: Int = 1,
    @ColumnInfo(name = "BRAND") val brand: Long? = null,
    @ColumnInfo(name = "IN_APP") val inApp: Boolean = false,
    @ColumnInfo(name = "SYNCHED") val synched: Boolean = false
) {
    @Ignore
    @SerializedName("barcodes")
    lateinit var barcodes: List<BarCode>

    @Ignore
    lateinit var barcode: String

    /**
     * Calculates the sell price details with discount on the HT total
     */
    fun calculateSellPriceDOnHT(quantity: Double = 0.0): Double {
        return (sellPriceDetailHT - if (promotion > 0.0 && quantity > promotionThreshold)
            calculatePercentageValue(sellPriceDetailHT, promotion) else 0.0) * quantity
    }

    /**
     * Calculates the sell price details with discount on the TTC total
     */
    fun calculateSellPriceDOnTTC(quantity: Double = 0.0): Double {
        return (sellPriceDetailTTC - if (promotion > 0.0 && quantity > promotionThreshold)
            calculatePercentageValue(sellPriceDetailHT, promotion) else 0.0) * quantity
    }

    /**
     * Calculates the sell price details with discount on the HT total
     */
    fun calculateSellPriceHWOnHT(quantity: Double = 0.0): Double {
        return (sellPriceHalfWholeHT - if (promotion > 0.0 && quantity > promotionThreshold)
            calculatePercentageValue(sellPriceDetailHT, promotion) else 0.0) * quantity
    }

    /**
     * Calculates the sell price details with discount on the TTC total
     */
    fun calculateSellPriceHWOnTTC(quantity: Double = 0.0): Double {
        return (sellPriceHalfWholeTTC - if (promotion > 0.0 && quantity > promotionThreshold)
            calculatePercentageValue(sellPriceDetailHT, promotion) else 0.0) * quantity
    }


    /**
     * Calculates the sell price details with discount on the HT total
     */
    fun calculateSellPriceWOnHT(quantity: Double = 0.0): Double {
        return (sellPriceWholeHT - if (promotion > 0.0 && quantity > promotionThreshold)
            calculatePercentageValue(sellPriceDetailHT, promotion) else 0.0) * quantity
    }

    /**
     * Calculates the sell price details with discount on the TTC total
     */
    fun calculateSellPriceWOnTTC(quantity: Double = 0.0): Double {
        return (sellPriceWholeTTC - if (promotion > 0.0 && quantity > promotionThreshold)
            calculatePercentageValue(sellPriceDetailHT, promotion) else 0.0) * quantity
    }

    fun getDiscountAmountOnDHT(): Double = calculatePercentageValue(sellPriceDetailHT, promotion)

    fun getTvaValue(): Double = calculatePercentageValue(sellPriceDetailHT, tva ?: 0.0)

    companion object {
        fun generateBarCode(): String {
            val charPool: List<Char> = ('0'..'5') + ('6'..'9')
            val randomString = (1..11)
                .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            return randomString
        }
    }

    fun toMap(): Map<Int, Any> {
        return mapOf(
            1 to this.barcode,
            2 to this.reference,
            3 to this.designation,
            4 to (this.purchasePriceHT ?: 0.0),
            5 to (this.tva ?: 0.0),
            6 to this.sellPriceDetailHT,
            7 to this.sellPriceHalfWholeHT,
            8 to this.sellPriceWholeHT,
        )
    }

    data class BarCode(val codeBareSyn: String)
}

@Entity(
    tableName = "expiration_dates", foreignKeys = [ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )], indices = [Index(value = ["PRODUCT"], unique = false)]
)
data class ExpirationDates(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "DATE") val date: Date,
    @ColumnInfo(name = "DISABLED", defaultValue = "0") val disabled: Boolean,
    @ColumnInfo(name = "PRODUCT") val product: Long
)

@Entity(tableName = "brands", indices = [Index(value = ["NAME"], unique = true)])
data class Brands(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "NAME") val name: String,
    @ColumnInfo(name = "IN_APP") val inApp: Boolean = false,
    @ColumnInfo(name = "SYNCHED") val synched: Boolean = false
) {
    override fun toString(): String = name
}

@Entity(
    primaryKeys = ["PRODUCT_ID", "PROVIDER_ID"],
    foreignKeys = [ForeignKey(
        entity = Products::class,
        parentColumns = arrayOf("PRODUCT_ID"),
        childColumns = arrayOf("PRODUCT_ID"),
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Providers::class,
        parentColumns = arrayOf("PROVIDER_ID"),
        childColumns = arrayOf("PROVIDER_ID")
    )],
    indices = [
        Index(value = ["PRODUCT_ID"], unique = false),
        Index(value = ["PROVIDER_ID"], unique = false)
    ]
)
data class ProductProviderCrossRef(
    @ColumnInfo(name = "PRODUCT_ID") val productId: Long,
    @ColumnInfo(name = "PROVIDER_ID") val providerId: Long
)

data class ProductWithProviders(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PROVIDER_ID",
        associateBy = Junction(ProductProviderCrossRef::class)
    ) val provider: List<Providers>
)

data class ProductWithBarcodes(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val barcodes: List<Barcodes>
)

data class BrandWithProducts(
    @Embedded val brand: Brands,
    @Relation(
        parentColumn = "id",
        entityColumn = "BRAND"
    ) val products: List<Products>
)

data class ProductWithExpirationDates(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val expirationDates: List<ExpirationDates>
)

data class AllAboutAProduct(
    @Embedded val product: Products,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val bardcodes: List<Barcodes>,
    @Relation(
        parentColumn = "PRODUCT_ID",
        entityColumn = "PRODUCT"
    ) val expirationDates: List<ExpirationDates>?,
    @Relation(
        parentColumn = "BRAND",
        entityColumn = "id"
    ) val brand: Brands?
) {
    override fun toString(): String = product.designation
}

