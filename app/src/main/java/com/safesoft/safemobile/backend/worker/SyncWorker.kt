package com.safesoft.safemobile.backend.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ClientsRepository
import io.reactivex.Single

class SyncWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val clientsRepository: ClientsRepository,
) : RxWorker(appContext, workersParams) {

    val TAG: String = this::class.simpleName!!

    fun synchronize() {

    }

    override fun createWork(): Single<Result> {
        TODO()
    }

}