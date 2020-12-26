package com.safesoft.safemobile

import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.safesoft.safemobile.backend.utils.formatted
import com.safesoft.safemobile.backend.utils.formattedForLog
import com.safesoft.safemobile.backend.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.plugins.RxJavaPlugins
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class SafeApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val syncRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS
        ).addTag("synchro_tag")
            .setConstraints(constraints)
            .build()

//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "synchronization",
//            ExistingPeriodicWorkPolicy.KEEP,
//            syncRequest
//        )

        WorkManager.getInstance(this).cancelAllWork()
        initializeToast()
        initializeErrorHandler()

        initializeLogger()
    }

    private fun initializeLogger() {
        Log.w("before", "Logcat save")
        try {
            val fileName = "Logging ${Date().formattedForLog()}.txt".replace(" ", "_")
            Log.d("Application", "initializeLogger: $fileName")
            var process = Runtime.getRuntime().exec("logcat -e")
            process =
                Runtime.getRuntime().exec("logcat -f /storage/emulated/0/$fileName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun initializeErrorHandler() {
        RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
        }
    }

    private fun initializeToast() {
        DynamicToast.Config.getInstance()
            .setDefaultBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorPrimaryDark
                )
            )
            .setErrorBackgroundColor(ContextCompat.getColor(applicationContext, R.color.error))
            .setSuccessBackgroundColor(ContextCompat.getColor(applicationContext, R.color.green))
            .setDefaultTintColor(ContextCompat.getColor(applicationContext, R.color.white))
            .setWarningBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.lightcoral
                )
            )
            .setWarningIcon(ContextCompat.getDrawable(applicationContext, R.drawable.adt_ic_error))
            .setSuccessIcon(
                ContextCompat.getDrawable(applicationContext, R.drawable.adt_ic_success)
            )
            .setErrorIcon(ContextCompat.getDrawable(applicationContext, R.drawable.error))
            .setTextSize(14)
            .apply()
    }

}