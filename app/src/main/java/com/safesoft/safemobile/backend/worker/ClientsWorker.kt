package com.safesoft.safemobile.backend.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ClientsRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ClientsWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val clientsRepository: ClientsRepository
) : RxWorker(appContext, workersParams) {

    private val io: Scheduler = Schedulers.io()

    val TAG: String = this::class.simpleName!!

    override fun createWork(): Single<Result> {
        return clientsRepository
            .loadClientsFromServer()
            .flatMapCompletable { clientsRepository.addClients(*it.clients.toTypedArray()) }
            .toSingleDefault(Result.success())
            .onErrorReturnItem(Result.failure())
            .observeOn(io)
    }

}