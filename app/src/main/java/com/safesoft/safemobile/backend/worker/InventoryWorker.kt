package com.safesoft.safemobile.backend.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAPurchase
import com.safesoft.safemobile.backend.db.local.entity.InventoryLines
import com.safesoft.safemobile.backend.db.local.entity.InventoryWithLines
import com.safesoft.safemobile.backend.db.local.entity.PurchaseLines
import com.safesoft.safemobile.backend.repository.InventoryRepository
import com.safesoft.safemobile.backend.repository.ProductsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class InventoryWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workersParams: WorkerParameters,
    private val inventoryRepository: InventoryRepository,
    private val productsRepository: ProductsRepository
) : RxWorker(appContext, workersParams) {
    val TAG = this::class.simpleName

    override fun createWork(): Single<Result> {
        return inventoryRepository
            .getAllInventoriesSync()
            .map {
                val newItems = mutableListOf<InventoryWithLines>()
                for (inventory in it) {
                    val newLinesList = mutableListOf<InventoryLines>()
                    for (line in inventory.lines) {
                        line.selectedProduct =
                            productsRepository.getAllAboutAProductByBarcode(line.barcode!!)
                                .blockingGet()
                        newLinesList.add(line)
                    }
                    newItems.add(inventory.copy(lines = newLinesList))
                }
                return@map newItems
            }
            .flatMap {
                if (it.isEmpty()) {
                    Log.d(TAG, "createWork: no purchases to sync")
                    return@flatMap Single.fromCallable { Result.success() }
                }
                inventoryRepository.insertInventoryInRemoteDB(*it.toTypedArray())
                    .toSingleDefault(Result.success())
            }
            .onErrorReturn {
                it.printStackTrace()
                Result.failure()
            }
            .doOnSuccess {
                inventoryRepository.markAllInventoriesAsSync().blockingGet()
                Log.d(TAG, "createWork: worker finished successfully")
            }
            .observeOn(Schedulers.io())
    }

}