package com.safesoft.safemobile.backend.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.api.wrapper.UpdatePurchaseWrapper
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.local.entity.ProductWithBarcodes
import com.safesoft.safemobile.backend.db.local.entity.PurchaseLines
import com.safesoft.safemobile.backend.repository.ProductsRepository
import com.safesoft.safemobile.backend.repository.PurchasesRepository
import com.safesoft.safemobile.backend.utils.formatted
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Flow


class PurchaseWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val purchasesRepository: PurchasesRepository,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {
    val TAG = this::class.simpleName


    override fun createWork(): Single<Result> {
        return purchasesRepository
            .getAllNewPurchasesWithAllInfo()
            .map {
                val newItems = mutableListOf<AllAboutAPurchase>()
                for (purchase in it) {
                    val newLinesList = mutableListOf<PurchaseLines>()
                    for (line in purchase.purchaseLines) {
                        line.selectedProduct =
                            productsRepository.getAllAboutAProductById(line.product).blockingGet()
                        newLinesList.add(line)
                    }
                    newItems.add(purchase.copy(purchaseLines = newLinesList))
                }
                return@map newItems
            }
            .flatMap {
                if (it.isEmpty()) {
                    Log.d(TAG, "createWork: no purchases to sync")
                    return@flatMap Single.fromCallable { Result.success() }
                }
                purchasesRepository.insertPurchasesInRemoteDB(*it.toTypedArray())
                    .toSingleDefault(Result.success())
            }
            .onErrorReturn {
                it.printStackTrace()
                Result.success()
            }
            .doOnSuccess {
                purchasesRepository.markAllPurchasesAsSync().blockingGet()
                Log.d(TAG, "createWork: worker finished successfully")
            }
            .observeOn(Schedulers.io())
    }


}