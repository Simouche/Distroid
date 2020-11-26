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

    override fun createWork(): Single<Result> {
        val products =
            listOf(
                Products(0, "produit6", "produit6", 50.0, 100.0, 19.0, 119.0),
                Products(1, "produit19", "produit19", 50.0, 100.0, 19.0, 119.0),
                Products(
                    1,
                    "produit1",
                    "produit1",
                    50.0,
                    100.0,
                    19.0,
                    119.0
                )
            )
        return Single.fromObservable {
            productsRepository.updateProducts(products).observeOn(Schedulers.io())
                .doOnSuccess {
                    Log.d("TAG", "createWork: success")
                    return@doOnSuccess
                }.doOnError { it.printStackTrace() }.subscribe()
        }

    }

}