package com.safesoft.safemobile.backend.model

data class DashBoard(
    val openPurchases: Int = 0,
    val validatedPurchases: Int = 0,
    val totalPurchases: Int = 0,
    val openSales: Int = 0,
    val validatedSales: Int = 0,
    val totalSales: Int = 0,
    val openInventory: Int = 0,
    val validatedInventory: Int = 0,
    val totalInventory: Int = 0,
    val productsOutOfStock: Int = 0,
    val productsExpired: Int = 0,
    val productsTotal: Int = 0,
    val totalClients: Int = 0,
    val totalProviders: Int = 0
)