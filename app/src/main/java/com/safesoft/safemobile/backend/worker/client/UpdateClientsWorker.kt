package com.safesoft.safemobile.backend.worker.client

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ClientsRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UpdateClientsWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val clientsRepository: ClientsRepository
) : RxWorker(appContext, workersParams) {

    private val io: Scheduler = Schedulers.io()

    val TAG: String = this::class.simpleName!!


    override fun createWork(): Single<Result> {
        return clientsRepository
            .getAllNewClients()
            .flatMap {
                if (it.isEmpty()) {
                    Log.d(TAG, "createWork: no clients to sync.")
                    return@flatMap Single.fromCallable { Result.success() }
                }
                clientsRepository.insertClientsInRemoteDB(it)
                    .toSingleDefault(Result.success())
            }.doOnSuccess {
                clientsRepository.markClientsAsSync().blockingGet()
            }.onErrorReturn {
                Log.d(TAG, "createWork: error happened!")
                it.printStackTrace()
                return@onErrorReturn Result.retry()
            }
            .onErrorReturnItem(Result.retry())
            .observeOn(io)
    }

}