package com.safesoft.safemobile.backend.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun getCurrentDateTime(): String {
    val theDate: String
    theDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy. HH:mm:ss")
        current.format(formatter)
    } else {
        val date = Date()
        val formatter = SimpleDateFormat("MM/dd/yyyy HH:mma", Locale.getDefault())
        formatter.format(date)
    }
    return theDate
}


fun calculatePercentageDiff(price1: Double, price2: Double): Double =
    (price1 / price2 - 1) * 100.0

fun calculatePercentageValue(price: Double, percentage: Double): Double =
    price * (percentage / 100.0)

fun calculateNewPrice(price: Double, percentage: Double): Double =
    price + calculatePercentageValue(price, percentage)

val PRINTER_SEPARATOR = """
                [L]
                [C]================================
                [L]
    """.trimIndent()

val PRINTER_MAIN_CONTENT_HEADER = """
    [L]<b>Produit</b> [C]<b>Qté</b> [C]<b>P.U</b> [R]<b>Total</b>
""".trimIndent()

val LINE_SEPARATOR = """
    [C]--------------------------------
""".trimIndent()

fun printerReceiptLine(designation: String, quantity: Double,unitPrice:Double, total: Double): String {
    return "[L]$designation [C]$quantity [C]$unitPrice [R]$total"
}
