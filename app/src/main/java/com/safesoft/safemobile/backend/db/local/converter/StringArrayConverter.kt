package com.safesoft.safemobile.backend.db.local.converter

import androidx.room.TypeConverter

class StringArrayConverter {
    @TypeConverter
    fun arrayFromString(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun arrayToString(values: List<String>?): String? {
        return values?.joinToString(",")
    }
}