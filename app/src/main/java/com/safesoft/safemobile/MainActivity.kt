package com.safesoft.safemobile

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.safesoft.safemobile.backend.printer.PrinterService
import com.safesoft.safemobile.viewmodel.AuthViewModel
import com.safesoft.safemobile.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isBound = false
    private lateinit var printerService: PrinterService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PrinterService.PrinterBinder
            printerService = binder.service
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val authViewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val topLevelDestinations = mutableSetOf(
        R.id.nav_dashboard,
        R.id.nav_products,
        R.id.nav_settings
    )

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        prepareDestinations()

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinations, drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onStart() {
        super.onStart()
        bindService()
    }

    private fun prepareDestinations() {
        if (authViewModel.hasPurchaseModule() && authViewModel.isBuyer()) {
            topLevelDestinations.addAll(arrayOf(R.id.nav_purchases, R.id.nav_providers))
            navView.menu[1].isEnabled = true
            navView.menu[6].isEnabled = true
        }

        if (authViewModel.hasSalesModule() && authViewModel.isSeller()) {
            topLevelDestinations.addAll(arrayOf(R.id.nav_sales, R.id.nav_clients))
            navView.menu[2].isEnabled = true
            navView.menu[5].isEnabled = true
        }

        if (authViewModel.hasInventoryModule() && authViewModel.isInventorier()) {
            topLevelDestinations.addAll(arrayOf(R.id.nav_inventory))
            navView.menu[4].isEnabled = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                authViewModel.logOut()
                val i: Intent? = baseContext.packageManager
                    .getLaunchIntentForPackage(baseContext.packageName)
                i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)

            }
            R.id.action_settings -> {
                if (getNavigationView().currentDestination?.id != R.id.nav_settings)
                    getNavigationView().navigate(R.id.action_global_nav_settings)
            }
            android.R.id.home -> {
                if (getNavigationView().currentDestination?.id !in topLevelDestinations)
                    return getNavigationView().popBackStack()
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START)
                else
                    drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

    private fun getNavigationView() =
        Navigation.findNavController(supportFragmentManager.primaryNavigationFragment!!.requireView())

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    private fun bindService() {
        val intent = Intent(this, PrinterService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbind() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    fun print() {
        printerService.print()
    }

    fun printSale(saleId: Long) {
        printerService.printSale(saleId)
    }

    fun printPurchase(purchaseId: Long) {
        printerService.printPurchase(purchaseId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbind()
    }

}