package com.safesoft.safemobile.ui.settings

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.*
import com.safesoft.safemobile.MainActivity
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.worker.ClientsWorker
import com.safesoft.safemobile.backend.worker.ProvidersWorker
import com.safesoft.safemobile.databinding.FragmentSynchronizationSettingsBinding
import com.safesoft.safemobile.ui.generics.BaseFragment
import com.safesoft.safemobile.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class SynchronizationSettings : BaseFragment() {

    private lateinit var binding: FragmentSynchronizationSettingsBinding

    private val viewModel: SettingsViewModel by viewModels(this::requireActivity)
    private val ids = mutableMapOf<String, UUID>()
    private val NOTIFICATION_ID = 888
    private val NOTIFICATION_CHANNEL = "PROGRESS"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    }

    override fun setUpObservers() {
        super.setUpObservers()
        binding.checkboxAutoSync.setOnCheckedChangeListener { _, b ->
            viewModel.setAutomaticSync(b)
        }
        binding.checkboxClients.setOnCheckedChangeListener { _, b ->
            viewModel.setSyncClientsModule(
                b
            )
        }
        binding.checkboxProviders.setOnCheckedChangeListener { _, b ->
            viewModel.setSyncProviderModule(
                b
            )
        }
        binding.checkboxProducts.setOnCheckedChangeListener { _, b ->
            viewModel.setSyncProductsModule(
                b
            )
        }
        binding.checkboxSales.setOnCheckedChangeListener { _, b ->
            viewModel.setSyncSalesModule(b)
        }
        binding.checkboxPurchases.setOnCheckedChangeListener { _, b ->
            viewModel.setSyncPurchasesModule(
                b
            )
        }
        binding.checkboxInventories.setOnCheckedChangeListener { _, b ->
            viewModel.setSyncInventoriesModule(
                b
            )
        }
        binding.checkboxTracking.setOnCheckedChangeListener { _, b ->
            viewModel.setSyncTrackingModule(
                b
            )
        }
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
    }

    private fun reinitialize() {

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