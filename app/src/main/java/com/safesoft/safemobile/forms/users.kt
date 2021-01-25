package com.safesoft.safemobile.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.backend.db.local.entity.Users
import com.safesoft.safemobile.backend.utils.asSHA256
import javax.inject.Inject


class UserFields @Inject constructor() {
    val username = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val birthDate = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val isAdmin = MutableLiveData<Boolean>().apply { value = false }
    val permission = MutableLiveData<MutableList<String>>().apply { value = mutableListOf() }

    fun save(): Users = Users(
        0,
        username.value!!,
        "123456789".asSHA256(),
        firstName.value,
        lastName.value,
        birthDate.value,
        address.value,
        phone.value,
        null,
        email.value,
        null,
        false,
        isAdmin.value!!,
        permission.value!!
    )
}

class UserErrorFields @Inject constructor() {
    var username: Int? = null
    var firstName: Int? = null
    var lastName: Int? = null
    var birthDate: Int? = null
    var address: Int? = null
    var phone: Int? = null
    var email: Int? = null
    var isAdmin: Int? = null
    var permission: Int? = null
}

class UserForm @Inject constructor(
    val fields: UserFields,
    val errors: UserErrorFields
) : BaseObservable() {

    @Bindable
    fun isValid(): Boolean {
        return true
    }

    @Bindable
    fun getUsernameError(): Int? = errors.username

    @Bindable
    fun getFirstNameError(): Int? = errors.firstName

    @Bindable
    fun getLastNameError(): Int? = errors.lastName

    @Bindable
    fun getBirthDateError(): Int? = errors.birthDate

    @Bindable
    fun getAddressError(): Int? = errors.address

    @Bindable
    fun getPhoneError(): Int? = errors.phone

    @Bindable
    fun getEmailError(): Int? = errors.email

    @Bindable
    fun getIsAdminError(): Int? = errors.isAdmin

    @Bindable
    fun getPermissionError(): Int? = errors.permission


}