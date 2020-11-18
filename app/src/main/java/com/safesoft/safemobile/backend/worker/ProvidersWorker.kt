package com.safesoft.safemobile.backend.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ProvidersRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ProvidersWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val providersRepository: ProvidersRepository
) : RxWorker(appContext, workersParams) {

    private val io: Scheduler = Schedulers.io()

    val TAG: String = this::class.simpleName!!

    override fun createWork(): Single<Result> {
        return providersRepository
            .loadProvidersFromServer()
            .flatMapCompletable { providersRepository.addProviders(*it.providers.toTypedArray()) }
            .toSingleDefault(Result.success())
            .onErrorReturn {
                Log.d(TAG, "createWork: error happened!")
                it.printStackTrace()
                return@onErrorReturn Result.retry()
            }
            .onErrorReturnItem(Result.retry())
            .observeOn(io)
    }

}