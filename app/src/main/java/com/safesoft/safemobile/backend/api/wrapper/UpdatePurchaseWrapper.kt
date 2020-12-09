package com.safesoft.safemobile.backend.api.wrapper

import com.safesoft.safemobile.backend.db.local.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.local.entity.ProductWithBarcodes

data class UpdatePurchaseWrapper(
    val purchases: List<AllAboutAPurchase>,
    val products: List<ProductWithBarcodes>
)