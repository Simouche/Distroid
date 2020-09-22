package com.safesoft.safemobile.ui.generics

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
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

    override fun onResume() {
        super.onResume()
        setUp()
        setupViews()
        setUpObservers()
    }
}