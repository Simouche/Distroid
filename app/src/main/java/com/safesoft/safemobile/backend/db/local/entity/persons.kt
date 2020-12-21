package com.safesoft.safemobile.backend.db.local.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName

data class FiscalData(
    @ColumnInfo(name = "REGISTRE_COMMERCE") val registreCommerce: String?,
    @ColumnInfo(name = "ARTCILE_FISCALE") val articleFiscale: String?,
    @ColumnInfo(name = "IDENTIFIANT_FISCALE") val identifiantFiscale: String?,
    @ColumnInfo(name = "IDENTIFIANT_STATISTIQUE") val identifiantStatistic: String?
)

data class Location(
    @ColumnInfo(name = "LONGITUDE") val longitude: Double,
    @ColumnInfo(name = "LATITUDE") val latitude: Double
)

@Entity(
    tableName = "providers",
    indices = [Index(value = ["CODE"], unique = true), Index(value = ["CLIENT"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Clients::class,
        parentColumns = arrayOf("CLIENT_ID"),
        childColumns = arrayOf("CLIENT"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Providers(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "PROVIDER_ID") val id: Long,
    @SerializedName("codeFournis") @ColumnInfo(name = "CODE") val code: String,
    @SerializedName("fournis") @ColumnInfo(name = "NAME") val name: String? = null,
    @ColumnInfo(name = "ACTIVITY") val activity: String? = null,
    @ColumnInfo(name = "ADDRESS") val address: String? = null,
    @ColumnInfo(name = "COMMUNE") val commune: String? = null,
    @ColumnInfo(name = "CONTACT") val contact: String? = null,
    @SerializedName("phone") @ColumnInfo(name = "PHONES") val phones: String? = null,
    @ColumnInfo(name = "FAXES") val faxes: String? = null,
    @ColumnInfo(name = "RIB") val rib: String? = null,
    @SerializedName("siteWeb") @ColumnInfo(name = "WEBSITE") val webSite: String? = null,
    @SerializedName("solde") @ColumnInfo(
        name = "SOLD",
        defaultValue = "0"
    ) val sold: Double? = null,
    @ColumnInfo(name = "PURCHASES", defaultValue = "0") val purchases: Double? = null,
    @ColumnInfo(name = "PAYMENT", defaultValue = "0") val payments: Double? = null,
    @SerializedName("notes") @ColumnInfo(name = "NOTE") val note: String? = null,
    /*@SerializedName("codeClient")*/ @ColumnInfo(name = "CLIENT") val client: Long? = null,
    @Embedded val fiscalData: FiscalData? = null,
    @Embedded val location: Location? = null,
    @ColumnInfo(name = "IN_APP") val inApp: Boolean = false,
    @ColumnInfo(name = "SYNCHED") val synched: Boolean = false
) {

    fun getAllPhones(): List<String>? {
        return phones?.split(";")?.map(String::trim)
    }

    fun getAllFaxes(): List<String>? {
        return faxes?.split(";")?.map(String::trim)
    }

    override fun toString(): String = "$code $name"

    fun toMap(): Map<Int, Any> {
        return mapOf(
            1 to this.code,
            2 to (this.name ?: " "),
            3 to (this.getAllPhones()?.get(0) ?: " "),
            4 to (this.address ?: " "),
            5 to (this.commune ?: " "),
        )
    }

    companion object {
        fun generateProviderCode(): String {
            val charPool: List<Char> = ('A'..'Z') + ('0'..'9')
            val randomString = (1..10)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            return randomString
        }
    }
}

data class ProviderWithProducts(
    @Embedded val provider: Providers,
    @Relation(
        parentColumn = "PROVIDER_ID",
        entityColumn = "PRODUCT_ID",
        associateBy = Junction(ProductProviderCrossRef::class)
    ) val product: List<Products>
)

data class ProviderWithPurchases(
    @Embedded val provider: Providers,
    @Relation(
        parentColumn = "PROVIDER_ID",
        entityColumn = "PROVIDER"
    ) val purchases: List<Purchases>
)


@Entity(
    tableName = "clients",
    indices = [Index(value = ["CODE"], unique = true), Index(value = ["PROVIDER"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Providers::class,
        parentColumns = arrayOf("PROVIDER_ID"),
        childColumns = arrayOf("PROVIDER"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Clients(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "CLIENT_ID") val id: Long,
    @SerializedName("codeClient") @ColumnInfo(name = "CODE") val code: String,
    @SerializedName("client") @ColumnInfo(name = "NAME") val name: String? = null,
    @SerializedName("activity") @ColumnInfo(name = "ACTIVITY") val activity: String? = null,
    @SerializedName("address") @ColumnInfo(name = "ADDRESS") val address: String? = null,
    @SerializedName("commune") @ColumnInfo(name = "COMMUNE") val commune: String? = null,
    @SerializedName("contact") @ColumnInfo(name = "CONTACT") val contact: String? = null,
    @SerializedName("phone") @ColumnInfo(name = "PHONES") val phones: String? = null,
    @SerializedName("fax") @ColumnInfo(name = "FAXES") val faxes: String? = null,
    @SerializedName("rib") @ColumnInfo(name = "RIB") val rib: String? = null,
    @SerializedName("siteweb") @ColumnInfo(name = "WEBSITE") val webSite: String? = null,
    @SerializedName("solde") @ColumnInfo(
        name = "SOLD",
        defaultValue = "0"
    ) val sold: Double? = null,
    @ColumnInfo(name = "SALES", defaultValue = "0") val sales: Double? = null,
    @ColumnInfo(name = "PAYMENT", defaultValue = "0") val payments: Double? = null,
    @SerializedName("note") @ColumnInfo(name = "NOTE") val note: String? = null,
    @ColumnInfo(name = "PROVIDER") val provider: Long? = null,
    @Embedded val fiscalData: FiscalData? = null,
    @Embedded val location: Location? = null,
    @ColumnInfo(name = "IN_APP") val inApp: Boolean = false,
    @ColumnInfo(name = "SYNCHED") val synched: Boolean = false
) {
    override fun toString(): String = "$code $name"

    fun getAllPhones(): List<String>? {
        return phones?.split(";")?.map(String::trim)
    }

    fun getAllFaxes(): List<String>? {
        return faxes?.split(";")?.map(String::trim)
    }

    fun toMap(): Map<Int, Any> {
        return mapOf(
            1 to this.code,
            2 to (this.name ?: " "),
            3 to (this.getAllPhones()?.get(0) ?: " "),
            4 to (this.address ?: " "),
            5 to (this.commune ?: " "),
        )
    }

    companion object {
        fun generateClientCode(): String {
            val charPool: List<Char> = ('A'..'Z') + ('0'..'9')
            val randomString = (1..10)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            return randomString
        }
    }
}


data class ProviderAndClient(
    @Embedded val provider: Providers,
    @Relation(
        parentColumn = "PROVIDER_ID",
        entityColumn = "id"
    )
    val client: Clients
)
