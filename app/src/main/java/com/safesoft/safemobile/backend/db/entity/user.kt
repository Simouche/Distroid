package com.safesoft.safemobile.backend.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.safesoft.safemobile.backend.utils.asSHA256

@Entity(tableName = "users", indices = [Index(value = ["USERNAME"], unique = true)])
data class Users(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "USERNAME") val username: String,
    @ColumnInfo(name = "PASSWORD") val password: String,
    @ColumnInfo(name = "FIRST_NAME") val firstName: String?,
    @ColumnInfo(name = "LAST_NAME") val lastName: String?,
    @ColumnInfo(name = "BIRTH_DATE") val birthDate: String?,
    @ColumnInfo(name = "ADDRESS") val address: String?,
    @ColumnInfo(name = "PHONE") val phone: String?,
    @ColumnInfo(name = "FAX") val fax: String?,
    @ColumnInfo(name = "EMAIL") val email: String?,
    @ColumnInfo(name = "LAST_LOGIN") val lastLogin: String?,
    @ColumnInfo(name = "LOGGED") val logged: Boolean
) {
    fun checkPassword(password: String): Boolean {
        return this.password == password.asSHA256()
    }
}