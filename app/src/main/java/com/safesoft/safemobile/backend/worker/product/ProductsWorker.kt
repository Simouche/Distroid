package com.safesoft.safemobile.backend.worker.product

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ProductsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ProductsWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {

    override fun createWork(): Single<Result> {
        return productsRepository.getAllProductsFromServer()
            .flatMapCompletable { productsRepository.addProducts(*it.products.toTypedArray()) }
            .toSingleDefault(Result.success())
            .onErrorReturn {
                it.printStackTrace()
                return@onErrorReturn Result.failure()
            }
            .observeOn(Schedulers.io())
    }
}