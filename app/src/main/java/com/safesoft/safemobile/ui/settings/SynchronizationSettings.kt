package com.safesoft.safemobile.ui.settings

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import com.safesoft.safemobile.MainActivity
import com.safesoft.safemobile.R
import com.safesoft.safemobile.databinding.FragmentSynchronizationSettingsBinding
import com.safesoft.safemobile.ui.generics.BaseAnimations
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class SynchronizationSettings : BaseFragment(), BaseAnimations {

    private lateinit var binding: FragmentSynchronizationSettingsBinding

    private val viewModel: SettingsViewModel by viewModels(this::requireActivity)
    private val ids = mutableMapOf<String, UUID>()
    private val NOTIFICATION_ID = 888
    private val NOTIFICATION_CHANNEL = "PROGRESS"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_synchronization_settings,
            container,
            false
        )
        return binding.root
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.syncPeriod.setSelection(viewModel.syncDuration.value!!)
        binding.reinitializeButton.setOnClickListener {
            viewModel.reinitialize()
            setUpWorksObservers()
        }
        binding.sendToServer.setOnClickListener {
            viewModel.sendUpdatesToRemoteDB()
            setUpWorksObservers()
        }
        binding.loadData.setOnClickListener {
            viewModel.downloadUpdatesFromRemoteDB()
            setUpWorksObservers()
        }
    }


    @ExperimentalStdlibApi
    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.autoSync.observe(viewLifecycleOwner, viewModel::setAutomaticSync)
        viewModel.clientsSync.observe(viewLifecycleOwner, viewModel::setSyncClientsModule)
        viewModel.providersSync.observe(viewLifecycleOwner, viewModel::setSyncProviderModule)
        viewModel.productsSync.observe(viewLifecycleOwner, viewModel::setSyncProductsModule)
        viewModel.salesSync.observe(viewLifecycleOwner, viewModel::setSyncSalesModule)
        viewModel.purchasesSync.observe(viewLifecycleOwner, viewModel::setSyncPurchasesModule)
        viewModel.inventoriesSync.observe(viewLifecycleOwner, viewModel::setSyncInventoriesModule)
        viewModel.trackingSync.observe(viewLifecycleOwner, viewModel::setSyncTrackingModule)
        viewModel.ipAddress.observe(viewLifecycleOwner, viewModel::setServerIp)
        viewModel.dbPath.observe(viewLifecycleOwner, viewModel::setDBPath)
        viewModel.warehouseCode.observe(viewLifecycleOwner, viewModel::setWarehouseCode)
        viewModel.syncDuration.observe(viewLifecycleOwner, binding.syncPeriod::setSelection)

        binding.syncPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setAutomaticSyncDuration(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: No Duration Selected!")
            }
        }

        binding.testConnectionButton.setOnClickListener {
            viewModel.testConnection().observe(viewLifecycleOwner, {
                when (it.state) {
                    loading -> {
                        binding.testConnectionButton.visibility = View.GONE
                        binding.loading.visibility = View.VISIBLE
                    }
                    success -> {
                        binding.testConnectionButton.visibility = View.VISIBLE
                        binding.loading.visibility = View.GONE
                        success(R.string.database_configured_success)
                    }
                    error -> {
                        binding.testConnectionButton.visibility = View.VISIBLE
                        binding.loading.visibility = View.GONE
                        val views = arrayOf(binding.ipAddressSetting, binding.databasePathSetting)
                        editTextErrorAnimation(
                            duration = 1000,
                            views = views
                        )

                        error(R.string.datanbase_configuration_error)
                    }
                }
            })
        }
    }

    private fun setUpWorksObservers() {
        viewModel.ids.keys.forEach {
            viewModel.workManagerInstance.getWorkInfoByIdLiveData(viewModel.ids[it]!!)
                .observe(viewLifecycleOwner, { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> showSynchronizationStartedDialog()
                        WorkInfo.State.SUCCEEDED -> showNotificationPerTask(it)
                        else -> return@observe
                    }
                })
        }

    }

    private fun showNotificationPerTask(id: String) {
        when (id) {
            "clearTables" -> showNotification(getString(R.string.reinitialization_complete))
            "clients", "clients_update" -> showNotification(getString(R.string.clients))
            "providers", "providers_update" -> showNotification(getString(R.string.providers))
            "product", "product_update" -> showNotification(getString(R.string.product))
            "purchase_update" -> showNotification(getString(R.string.purchases))
            "sales_update" -> showNotification(getString(R.string.sales))
        }
    }


    private fun showNotification(task: String) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val builder = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(getString(R.string.synchronization))
            .setContentText(getString(R.string.sync_completed, task))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notification: Notification = builder.build()

        with(NotificationManagerCompat.from(requireContext())) {
            notify(101, notification)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = getString(R.string.synchronization_channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}