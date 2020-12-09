package com.safesoft.safemobile.backend.worker.product

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ProductsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UpdateProductsWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {
    val TAG = this::class.simpleName

    override fun createWork(): Single<Result> {
        return productsRepository
            .getAllNewProductsWithBarCodes()
            .flatMap { productsRepository.updateProducts(it) }
            .map { Result.success() }
            .observeOn(Schedulers.io())
            .onErrorReturn { Result.retry() }
    }

}