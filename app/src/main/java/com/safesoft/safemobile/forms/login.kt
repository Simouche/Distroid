package com.safesoft.safemobile.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.safesoft.safemobile.BR
import com.safesoft.safemobile.R
import javax.inject.Inject

class LoginFields @Inject constructor() {

    var username: String? = null
    var password: String? = null
}

class LoginErrorFields @Inject constructor() {
    var username: Int? = null
    var password: Int? = null
}

class LoginForm @Inject constructor(
    var fields: LoginFields?,
    var errors: LoginErrorFields?
) : BaseObservable() {
    init {
        fields?.username = null
        fields?.password = null
        errors?.username = null
        errors?.password = null
    }

    @Bindable
    fun isValid(): Boolean {
        val valid = isUsernameValid(false) && isPasswordValid(false)
        notifyPropertyChanged(BR.usernameError)
        notifyPropertyChanged(BR.passwordError)
        return valid
    }

    fun isUsernameValid(setMessage: Boolean): Boolean {
        val username: String? = fields?.username
        return if (username != null && username.length >= 5) {
            // TODO: 8/17/2020 validation here
            errors?.username = null
            notifyPropertyChanged(BR.valid)
            true
        } else {
            if (setMessage) {
                errors?.username = R.string.invalid_username
                notifyPropertyChanged(BR.valid)
            }
            false
        }
    }

    fun isPasswordValid(setMessage: Boolean): Boolean {
        val password: String? = fields?.password
        return if (fields?.password != null && password?.length!! > 5) {
            errors?.password = null
            notifyPropertyChanged(BR.valid)
            true
        } else {
            if (setMessage) {
                errors?.password = R.string.short_password
                notifyPropertyChanged(BR.valid)
            }
            false
        }
    }


    @Bindable
    fun getUsernameError(): Int? = errors?.username

    @Bindable
    fun getPasswordError(): Int? = errors?.password
}
