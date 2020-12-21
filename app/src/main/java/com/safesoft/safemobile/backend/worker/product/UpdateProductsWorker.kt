package com.safesoft.safemobile.backend.worker.product

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.db.local.entity.Barcodes
import com.safesoft.safemobile.backend.repository.ProductsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UpdateProductsWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {
    val TAG = this::class.simpleName

    @SuppressLint("CheckResult")
    override fun createWork(): Single<Result> {
        return productsRepository
            .getAllNewProductsWithBarCodes()
            .map {
                if (it.isEmpty()) {
                    Log.d(TAG, "createWork: no products to sync.")
                    return@map true
                }
                Log.d(TAG, "products selected for send: $it")
                try {
                    val err = productsRepository.insertProductsInRemoteDB(it.map { it2 ->
                        it2.product.barcode = it2.barcodes[0].code
                        it2.product
                    }).blockingGet()
                    if (err != null) {
                        Log.d(TAG, "createWork: failed inserting products", err)
                        return@map false
                    }
                    Log.d(TAG, "createWork: finished inserting products.")
                    val barCodes: MutableList<Barcodes> = mutableListOf()
                    it.map { it2 ->
                        it2.barcodes.map { barcode ->
                            barcode.mainBarCode = it2.barcodes[0].code
                            barCodes.add(barcode)
                        }
                    }
                    productsRepository.insertProductBarCodesInRemoteDB(barCodes).blockingGet()
                    Log.d(TAG, "createWork: finished inserting barCodes")
                    productsRepository.markProductsAsSynched().blockingGet()
                    Log.d(TAG, "createWork: finished marking products as sync.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    e
                }
            }
            .doOnError {
                it.printStackTrace()
            }
            .map { Result.success() }
            .observeOn(Schedulers.io())
            .onErrorReturn { Result.retry() }
    }

}