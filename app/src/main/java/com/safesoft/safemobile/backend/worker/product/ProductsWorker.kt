package com.safesoft.safemobile.backend.worker.product

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.repository.ProductsRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking

class ProductsWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {

    val TAG = this::class.simpleName

    override fun createWork(): Single<Result> {
        return productsRepository
            .loadProductsFromRemoteDB() // Load the products from the server
            .map {
                productsRepository.addProducts(*it.toTypedArray()) // save all the added products to the local DB
                    .subscribe({ ids -> // the ids of the saved products rows
                        it.mapIndexed { index, product -> //for each product returned from the RemoteDB
                            productsRepository
                                .loadProductBarCodesFromRemoteDB(
                                    where = "CODE_BARRE = ?",
                                    whereArgs = mapOf(1 to product.barcode)
                                ) //fetch the products barcodes
                                .subscribe({ barcodes ->
                                    try {
                                        runBlocking {
                                            productsRepository.addBarCodes(*barcodes.map { barcode -> // foreach selected barcode
                                                barcode.copy(product = ids[index]) // assign the barcode to his product
                                            }.toTypedArray())
                                        }
                                    } catch (e: SQLiteConstraintException) {
                                        Log.d(
                                            TAG,
                                            "createWork: failed to insert this barcode:$barcodes"
                                        )
                                    }
                                }, { err ->
                                    err.printStackTrace()
                                })
                        }
                    },
                        { err ->
                            err.printStackTrace()
                            Result.failure()
                        }
                    )

                Result.success()
            }
            .onErrorReturn {
                it.printStackTrace()
                return@onErrorReturn Result.failure()
            }
            .observeOn(Schedulers.io())
    }
}