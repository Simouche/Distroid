package com.safesoft.safemobile.backend.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.api.wrapper.UpdatePurchaseWrapper
import com.safesoft.safemobile.backend.db.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.entity.ProductWithBarcodes
import com.safesoft.safemobile.backend.repository.ProductsRepository
import com.safesoft.safemobile.backend.repository.PurchasesRepository
import com.safesoft.safemobile.backend.utils.formatted
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers


class PurchaseWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val purchasesRepository: PurchasesRepository,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {
    val TAG = this::class.simpleName


    override fun createWork(): Single<Result> {
        return purchasesRepository
            .getAllPurchasesWithAllInfo()
            .zipWith(productsRepository.getAllProductsWithBarCodesSingle(),
                BiFunction<List<AllAboutAPurchase>, List<ProductWithBarcodes>, UpdatePurchaseWrapper> { var1, var2 ->
                    val tweaked = var1.map { it2 ->
                        it2.purchase.stringDate = it2.purchase.date?.formatted() ?: ""
                        return@map it2
                    }
                    return@BiFunction UpdatePurchaseWrapper(tweaked, var2)
                })
            .flatMap { purchasesRepository.updatePurchases(it) }
            .map { Result.success() }
            .onErrorReturn {
                it.printStackTrace()
                Result.retry()
            }
            .observeOn(Schedulers.io())
    }


}