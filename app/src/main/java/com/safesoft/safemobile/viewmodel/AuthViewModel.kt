package com.safesoft.safemobile.viewmodel

import android.view.View
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.repository.AuthRepository
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.backend.utils.formatted
import com.safesoft.safemobile.backend.utils.setError
import com.safesoft.safemobile.forms.LoginForm
import com.safesoft.safemobile.forms.UserForm
import java.util.*
import com.safesoft.safemobile.backend.db.local.entity.Users as entityUser


class AuthViewModel @ViewModelInject constructor(
    val loginForm: LoginForm,
    val userForm: UserForm,
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
) : BaseViewModel() {

    init {
        modules = preferencesRepository.getActiveModules()!!
    }

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

    fun checkPassword(nUser: entityUser): Boolean {
        user = nUser
        return user.checkPassword(loginForm.fields?.password!!)
    }

    fun login(): LiveData<Resource<Boolean>> {
        user = user.copy(logged = true, lastLogin = Calendar.getInstance().time.formatted())
        return updateUser(user)
    }

    private fun updateUser(user: entityUser): LiveData<Resource<Boolean>> {
        val data = MutableLiveData<Resource<Boolean>>()
        enqueue(authRepository.update(user), data)
        return data
    }

    fun checkLogged(): LiveData<Resource<entityUser>> {
        val data = MutableLiveData<Resource<entityUser>>()
        enqueue(authRepository.checkLogged(), data)
        return data
    }

    fun logOut() =
        enqueue(authRepository.logOut(), MutableLiveData<Resource<Boolean>>())

    fun isAdmin(): Boolean = user.isAdmin

    fun setAdmin(user: entityUser): LiveData<Resource<Boolean>> {
        val nUser = user.copy(isAdmin = true)
        return updateUser(nUser)
    }

    fun isBuyer(): Boolean = user.hasPerm("B")

    fun setBuyer(user: entityUser): LiveData<Resource<Boolean>> {
        val nUser = user.copy(permissions = user.permissions + listOf("B"))
        return updateUser(nUser)
    }

    fun isSeller(): Boolean = user.hasPerm("S")

    fun setSeller(user: entityUser): LiveData<Resource<Boolean>> {
        val nUser = user.copy(permissions = user.permissions + listOf("S"))
        return updateUser(nUser)
    }

    fun isInventorier(): Boolean = user.hasPerm("I")

    fun setInventorier(user: entityUser): LiveData<Resource<Boolean>> {
        val nUser = user.copy(permissions = user.permissions + listOf("I"))
        return updateUser(nUser)
    }

    fun hasPurchaseModule(): Boolean = "P" in modules

    fun activatePurchaseModule() {
        modules = modules + setOf("P")
        saveActiveModules()
    }

    fun hasSalesModule(): Boolean = "S" in modules

    fun activateSalesModule() {
        modules = modules + setOf("S")
        saveActiveModules()
    }

    fun hasInventoryModule(): Boolean = "I" in modules

    fun activateInventoryModule() {
        modules = modules + setOf("I")
        saveActiveModules()
    }

    fun activateAllModules() {
        modules = modules + setOf("P", "S", "I")
        saveActiveModules()
    }

    private fun saveActiveModules() {
        preferencesRepository.setActiveModules(modules)
    }

    fun getAllUsers(): LiveData<Resource<List<entityUser>>> {
        val data = MutableLiveData<Resource<List<entityUser>>>()
        enqueue(authRepository.getAll(), data)
        return data
    }

    fun saveButton() =
         createUser(userForm.fields.save())


    private fun createUser(user: entityUser): LiveData<Resource<entityUser>> {
        val data = MutableLiveData<Resource<entityUser>>()
        enqueue(authRepository.insert(user), data)
        return data
    }

    fun deleteUser(user: entityUser): LiveData<Resource<Boolean>> {
        val data = MutableLiveData<Resource<Boolean>>()
        enqueue(authRepository.delete(user), data)
        return data
    }

    companion object {
        lateinit var user: entityUser
        lateinit var modules: Set<String>
    }

}

