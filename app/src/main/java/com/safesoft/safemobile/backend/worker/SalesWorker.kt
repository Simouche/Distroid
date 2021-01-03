package com.safesoft.safemobile.backend.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.local.entity.AllAboutASale
import com.safesoft.safemobile.backend.db.local.entity.PurchaseLines
import com.safesoft.safemobile.backend.db.local.entity.SaleLines
import com.safesoft.safemobile.backend.repository.ProductsRepository
import com.safesoft.safemobile.backend.repository.ProvidersRepository
import com.safesoft.safemobile.backend.repository.SalesRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class SalesWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val salesRepository: SalesRepository,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {
    private val io: Scheduler = Schedulers.io()

    val TAG: String = this::class.simpleName!!


    override fun createWork(): Single<Result> {
        return salesRepository
            .getAllNewSalesWithAllInfo()
            .map {
                val newItems = mutableListOf<AllAboutASale>()
                for (sale in it) {
                    val newLinesList = mutableListOf<SaleLines>()
                    for (line in sale.saleLines) {
                        line.selectedProduct =
                            productsRepository.getAllAboutAProductById(line.product).blockingGet()
                        newLinesList.add(line)
                    }
                    newItems.add(sale.copy(saleLines = newLinesList))
                }
                return@map newItems
            }
            .flatMap {
                if (it.isEmpty()) {
                    Log.d(TAG, "createWork: no purchases to sync")
                    return@flatMap Single.fromCallable { Result.success() }
                }
                salesRepository.insertSalesInRemoteDB(*it.toTypedArray())
                    .toSingleDefault(Result.success())
            }
            .onErrorReturn {
                it.printStackTrace()
                Result.success()
            }
            .doOnSuccess {
                salesRepository.markAllSalesAsSync().blockingGet()
                Log.d(TAG, "createWork: worker finished successfully")
            }
            .observeOn(Schedulers.io())
    }
}