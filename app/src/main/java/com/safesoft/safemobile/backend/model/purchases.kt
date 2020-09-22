package com.safesoft.safemobile.backend.model

import com.safesoft.safemobile.backend.db.entity.Products

data class PurchaseLineModel(
    val product: Products,
    val quantity: Double,
    val buyPriceHt: Double,
    val buyPriceTTC: Double,
    val tva: Double,
    val note: String,
    val purchase: Long?
)