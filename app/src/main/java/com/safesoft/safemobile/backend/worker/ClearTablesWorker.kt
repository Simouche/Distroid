package com.safesoft.safemobile.backend.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.SyncRepository
import io.reactivex.Single

class ClearTablesWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : RxWorker(appContext, workersParams) {


    override fun createWork(): Single<Result> {
        return syncRepository.clearAllTables().toSingleDefault(Result.success())
    }
}