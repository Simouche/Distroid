package com.safesoft.safemobile.backend.worker.product

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ProductsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ProductStockWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {

    val TAG = this::class.simpleName


    override fun createWork(): Single<Result> {
        return productsRepository.loadStock().subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io()).map {
                val results = it.map { map ->
                    val result = productsRepository.setProductStock(
                        (map["STOCK"] as Double),
                        (map["BARCODE"] as String)
                    ).blockingGet()
                    Log.d(
                        TAG,
                        "the stock of ${map["BARCODE"]} is ${map["STOCK"]} and it is saved: ${result == null}"
                    )
                }
                Result.success()
            }.doOnSuccess {
                Log.d(TAG, "createWork: Stocks updated successfully")
            }.doOnError {
                Log.d(TAG, "createWork: Stocks update failed! with error:")
                it.printStackTrace()
            }
    }

}