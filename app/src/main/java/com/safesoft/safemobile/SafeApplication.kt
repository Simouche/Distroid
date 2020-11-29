package com.safesoft.safemobile

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.safesoft.safemobile.backend.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.util.concurrent.Executors
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
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}