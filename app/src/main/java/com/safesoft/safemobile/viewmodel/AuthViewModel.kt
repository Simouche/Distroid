package com.safesoft.safemobile.viewmodel

import android.view.View
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.repository.AuthRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.backend.utils.setError
import com.safesoft.safemobile.forms.LoginForm
import java.util.*
import com.safesoft.safemobile.backend.db.local.entity.Users as entityUser


class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    val loginForm: LoginForm
) : BaseViewModel() {

    private lateinit var user: entityUser

    val onFocusUsername: View.OnFocusChangeListener =
        View.OnFocusChangeListener { view, focused ->
            run {
                val et = view as EditText
                if (et.text.isNotEmpty() && !focused)
                    loginForm.isUsernameValid(true)
            }
        }

    val onFocusPassword: View.OnFocusChangeListener = View.OnFocusChangeListener { view, b ->
        run {
            val et = view as EditText
            if (et.text.isNotEmpty() && !b)
                loginForm.isPasswordValid(true)
        }
    }

    fun attemptLogin(): LiveData<Resource<entityUser>> {
        val data: MutableLiveData<Resource<entityUser>> = MutableLiveData()
        if (!loginForm.isValid())
            return data.also { it.setError(messageId = R.string.invalid_credentials) }

        enqueue(authRepository.attemptLogin(username = loginForm.fields?.username!!), data)
        return data
    }

    fun checkPassword(user: entityUser): Boolean {
        this.user = user
        return this.user.checkPassword(loginForm.fields?.password!!)
    }

    fun login() {
        val loggedUser =
            this.user.copy(logged = true, lastLogin = Calendar.getInstance().time.toString())
        enqueue(authRepository.update(loggedUser), MutableLiveData<Resource<Boolean>>())
    }

    fun checkLogged(): LiveData<Resource<entityUser>> {
        val data = MutableLiveData<Resource<entityUser>>()
        enqueue(authRepository.checkLogged(), data)
        return data
    }

    fun logOut() =
        enqueue(authRepository.logOut(), MutableLiveData<Resource<Boolean>>())

}

