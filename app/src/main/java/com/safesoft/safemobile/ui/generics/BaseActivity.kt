package com.safesoft.safemobile.ui.generics

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.safesoft.safemobile.backend.utils.ResourceState

abstract class BaseActivity : AppCompatActivity() {
    val loading = ResourceState.LOADING
    val success = ResourceState.SUCCESS
    val error = ResourceState.ERROR

    protected open fun setupViews() {}

    protected open fun setUpObservers() {}
    protected open fun setUp() {}

    protected fun toast(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun success(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            DynamicToast.makeSuccess(applicationContext, getString(messageId), Toast.LENGTH_LONG)
        else
            DynamicToast.makeSuccess(applicationContext, message, Toast.LENGTH_LONG)
    }

    protected fun error(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            DynamicToast.makeError(applicationContext, getString(messageId), Toast.LENGTH_LONG)
        else
            DynamicToast.makeError(applicationContext, message, Toast.LENGTH_LONG)
    }

    protected fun warning(@StringRes messageId: Int? = null, message: String? = null) {
        if (messageId != null)
            DynamicToast.makeWarning(applicationContext, getString(messageId), Toast.LENGTH_LONG)
        else
            DynamicToast.makeWarning(applicationContext, message, Toast.LENGTH_LONG)
    }

    override fun onResume() {
        super.onResume()
        setUp()
        setupViews()
        setUpObservers()
    }
}