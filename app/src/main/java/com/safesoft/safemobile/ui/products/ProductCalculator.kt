package com.safesoft.safemobile.ui.products

interface ProductCalculator {

    fun calculatePercentageDiff(price1: Double, price2: Double): Double =
        (price1 / price2 - 1) * 100.0

    fun calculatePercentage(price: Double, percentage: Double): Double = price * percentage / 100.0

    fun calculateNewPrice(price: Double, percentage: Double): Double =
        price + calculatePercentage(price, percentage)





}