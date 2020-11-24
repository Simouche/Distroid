package com.safesoft.safemobile.backend.utils

import androidx.lifecycle.MutableLiveData
import java.math.BigInteger
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat


//extension function into the String class
fun String.asSHA256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

// extension functions into the LiveData class
fun <T> MutableLiveData<Resource<T>>.setSuccess(data: T? = null) =
    postValue(Resource(ResourceState.SUCCESS, data))

fun <T> MutableLiveData<Resource<T>>.setLoading() =
    postValue(Resource(ResourceState.LOADING, value?.data))

fun <T> MutableLiveData<Resource<T>>.setError(
    message: String? = null, exception: Throwable? = null, messageId: Int? = null
) = postValue(Resource(ResourceState.ERROR, value?.data, message, messageId, exception))


fun CharSequence.isEmptyOrBlank(): Boolean = this.isEmpty() || this.isBlank()

fun Double.formatted(
    format: String = "#,##0.00",
    roundingMode: RoundingMode = RoundingMode.CEILING
): String {
    val df = DecimalFormat(format)
    df.roundingMode = roundingMode
    return df.format(this)
}

fun String.doubleValue(): Double {
    return (if (this.isEmptyOrBlank() || this.justADot()) "0.0" else this).replace(",", "")
        .toDouble()
}

fun String.justADot(): Boolean {
    return this.length == 1 && this.contains(".")
}