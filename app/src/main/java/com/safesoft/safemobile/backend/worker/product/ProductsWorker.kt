package com.safesoft.safemobile.backend.worker.product

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.db.local.entity.Barcodes
import com.safesoft.safemobile.backend.repository.ProductsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking

class ProductsWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {

    val TAG = this::class.simpleName

    override fun createWork(): Single<Result> {
        return productsRepository.getAllProductsFromServer()
            .map {
                it.products.map { product ->
                    val rowId = productsRepository.addProduct(product).blockingGet()
                    val barcodes = mutableListOf<Barcodes>()
                    for (barcode in product.barcodes)
                        barcodes.add(Barcodes(0, barcode.codeBareSyn, rowId))
                    try {
                        runBlocking { productsRepository.addBarCodes(*barcodes.toTypedArray()) }
                    } catch (e: SQLiteConstraintException) {
                        Log.d(TAG, "createWork: failed to insert this barcode:$barcodes")
                    }
                }
                Result.success()
            }
            .onErrorReturn {
                it.printStackTrace()
                return@onErrorReturn Result.failure()
            }
            .observeOn(Schedulers.io())
    }
}