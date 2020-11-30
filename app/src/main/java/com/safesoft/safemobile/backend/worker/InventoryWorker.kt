package com.safesoft.safemobile.backend.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.InventoryRepository
import io.reactivex.Single

class InventoryWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val inventoryRepository: InventoryRepository
) : RxWorker(appContext, workersParams) {
    override fun createWork(): Single<Result> {
        TODO()
    }
}