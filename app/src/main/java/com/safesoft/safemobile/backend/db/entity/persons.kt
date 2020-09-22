package com.safesoft.safemobile.backend.db.entity

import androidx.room.*

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
    @ColumnInfo(name = "CODE") val code: String,
    @ColumnInfo(name = "NAME") val name: String? = null,
    @ColumnInfo(name = "ACTIVITY") val activity: String? = null,
    @ColumnInfo(name = "ADDRESS") val address: String? = null,
    @ColumnInfo(name = "COMMUNE") val commune: Long? = null,
    @ColumnInfo(name = "CONTACT") val contact: String? = null,
    @ColumnInfo(name = "PHONES") val phones: String? = null,
    @ColumnInfo(name = "FAXES") val faxes: String? = null,
    @ColumnInfo(name = "RIB") val rib: String? = null,
    @ColumnInfo(name = "WEBSITE") val webSite: String? = null,
    @ColumnInfo(name = "INITIAL_SOLD", defaultValue = "0") val initialSold: Double? = null,
    @ColumnInfo(name = "NOTE") val note: String? = null,
    @ColumnInfo(name = "CLIENT") val client: Long? = null,
    @Embedded val fiscalData: FiscalData? = null,
    @Embedded val location: Location? = null
) {

    fun getAllPhones(): List<String>? {
        return phones?.split(";")?.map(String::trim)
    }

    fun getAllFaxes(): List<String>? {
        return faxes?.split(";")?.map(String::trim)
    }

    override fun toString(): String = "$code $name"

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
    @ColumnInfo(name = "CODE") val code: String,
    @ColumnInfo(name = "NAME") val name: String? = null,
    @ColumnInfo(name = "ACTIVITY") val activity: String? = null,
    @ColumnInfo(name = "ADDRESS") val address: String? = null,
    @ColumnInfo(name = "COMMUNE") val commune: Long? = null,
    @ColumnInfo(name = "CONTACT") val contact: String? = null,
    @ColumnInfo(name = "PHONES") val phones: String? = null,
    @ColumnInfo(name = "FAXES") val faxes: String? = null,
    @ColumnInfo(name = "RIB") val rib: String? = null,
    @ColumnInfo(name = "WEBSITE") val webSite: String? = null,
    @ColumnInfo(name = "INITIAL_SOLD", defaultValue = "0") val initialSold: Double? = null,
    @ColumnInfo(name = "NOTE") val note: String? = null,
    @ColumnInfo(name = "PROVIDER") val provider: Long? = null,
    @Embedded val fiscalData: FiscalData? = null,
    @Embedded val location: Location? = null
) {
    override fun toString(): String = "$code $name"

    fun getAllPhones(): List<String>? {
        return phones?.split(";")?.map(String::trim)
    }

    fun getAllFaxes(): List<String>? {
        return faxes?.split(";")?.map(String::trim)
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
