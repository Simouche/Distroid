package com.safesoft.safemobile.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.room.EmptyResultSetException
import com.safesoft.safemobile.MainActivity
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.Users
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.databinding.ActivityLoginBinding
import com.safesoft.safemobile.ui.generics.BaseActivity
import com.safesoft.safemobile.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        binding.login.setOnClickListener {
            viewModel.attemptLogin().observe(this, Observer {
                when (it.state) {
                    loading -> {
                        Log.d("LoginActivity", "Loading: Attempting login")
                        binding.login.visibility = View.GONE
                        binding.loading.visibility = View.VISIBLE
                    }
                    success -> {
                        Log.d("LoginActivity", "Success: Success login")
                        if (viewModel.checkPassword(it.data!!)) {
                            toast(R.string.welcome)
                            viewModel.login()
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                            return@Observer
                        }
                        binding.login.visibility = View.VISIBLE
                        binding.loading.visibility = View.GONE
                        toast(messageId = R.string.invalid_credentials)
                        return@Observer
                    }
                    error -> {
                        Log.d("LoginActivity", "Error: Attempting login")
                        binding.login.visibility = View.VISIBLE
                        binding.loading.visibility = View.GONE
                        if (it.exception is EmptyResultSetException) {
                            toast(messageId = R.string.invalid_credentials)
                            return@Observer
                        }
                        toast(messageId = it.messageId ?: R.string.unkown_error)
                        it.exception?.printStackTrace()
                    }
                    else -> return@Observer
                }
            }
            )
        }
    }

    override fun setUp() {
        super.setUp()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

}