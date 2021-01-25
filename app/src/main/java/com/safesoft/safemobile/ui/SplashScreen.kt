package com.safesoft.safemobile.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.safesoft.safemobile.MainActivity
import com.safesoft.safemobile.R
import com.safesoft.safemobile.ui.auth.LoginActivity
import com.safesoft.safemobile.ui.generics.BaseActivity
import com.safesoft.safemobile.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.Authenticator
import java.util.*

@AndroidEntryPoint
class SplashScreen : BaseActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var mIntent: Intent


    private val permissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.BLUETOOTH
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        val cour = GlobalScope.launch {
            delay(5000)
            while (true)
                if (this@SplashScreen::mIntent.isInitialized) {
                    permissionCheck()
                    break
                }
        }

        viewModel.checkLogged().observe(this@SplashScreen, Observer {
            when (it.state) {
                loading -> {
                    return@Observer
                }
                success -> {
                    mIntent = Intent(applicationContext, MainActivity::class.java)
                    AuthViewModel.user = it.data!!
                    viewModel.login()
                }
                error -> {
                    mIntent = Intent(applicationContext, LoginActivity::class.java)
                    it.exception?.printStackTrace()
                }
            }
        })
    }

    private fun permissionCheck() {
        Permissions.check(
            this,
            permissions,
            null,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    startActivity(mIntent)
                    finish()
                }

                override fun onDenied(
                    context: Context?,
                    deniedPermissions: ArrayList<String>?
                ) {
                    super.onDenied(context, deniedPermissions)
                    finishAffinity()
                }
            })
    }

}