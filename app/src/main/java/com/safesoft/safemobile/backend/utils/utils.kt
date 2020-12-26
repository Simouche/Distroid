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

fun calculatePercentageValue(price: Double, percentage: Double): Double = price * (percentage / 100.0)

fun calculateNewPrice(price: Double, percentage: Double): Double =
    price + calculatePercentageValue(price, percentage)



