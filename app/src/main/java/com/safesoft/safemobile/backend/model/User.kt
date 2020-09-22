package com.safesoft.safemobile.backend.model

abstract class Model(val id: Int?, val createdAt: String?)

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val firstName: String?,
    val lastName: String?,
    val birthDate: String?,
    val address: String?,
    val phone: String?,
    val fax: String?,
    val email: String?,
    val lastLogin: String?,
    val logged: Boolean
) {




}