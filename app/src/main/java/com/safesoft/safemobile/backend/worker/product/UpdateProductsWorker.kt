package com.safesoft.safemobile.backend.worker.product

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.db.entity.Products
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
        return Single.fromObservable {
            productsRepository
                .getAllProductsWithBarCodesSingle()
                .flatMap { productsRepository.updateProducts(it) }
                .doOnSuccess { Log.d(TAG, "createWork: finished sending products to server") }
                .doOnError { it.printStackTrace() }
                .observeOn(Schedulers.io())
                .subscribe()
        }
    }

/*
    return  productsRepository.updateProducts(products).doOnSuccess {
        Log.d(
            TAG,
            "createWork: syccess"
        ) }.map { Result.success() }.onErrorReturn { Result.failure() }*/
}