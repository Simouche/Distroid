package com.safesoft.safemobile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.safesoft.safemobile.backend.db.entity.*
import com.safesoft.safemobile.backend.repository.InventoryRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.forms.InventoryForm
import com.safesoft.safemobile.ui.products.ProductCalculator
import java.util.*

class InventoryViewModel @ViewModelInject constructor(
    private val repository: InventoryRepository,
    private val config: PagedList.Config,
    val inventoryForm: InventoryForm
) : BaseViewModel(), ProductCalculator {

    val label = MutableLiveData<String>()
    val numero = MutableLiveData<String>()

    val inventory = MutableLiveData<Inventories>().apply {
        value = Inventories(
            id = 0,
            numero = 0,
            Date(),
            time = "",
            label = "",
            productsCount = 0,
            htAmount = 0.0,
            tvaAmount = 0.0,
            newHTAmount = 0.0,
            newTVAAmount = 0.0,
            htDifference = 0.0,
            tvaDifference = 0.0,
            status = "O"
        )
    }

    private val tag = InventoryViewModel::class.simpleName

    val searchQuery = MutableLiveData<String>()

    val inventoriesList: LiveData<PagedList<InventoryWithLines>> =
        repository.getAllInventories().toLiveData(config = config)


    fun search(query: String): LiveData<PagedList<InventoryWithLines>> =
        repository.searchInventory(query).toLiveData(config = config)

    fun addLine(product: AllAboutAProduct?, physicalQuantity: Double): InventoryLines {
        val line = InventoryLines(
            id = 0,
            inventory = 0,
            barcode = product?.bardcodes?.first()?.id,
            lot = "",
            buyPriceHT = product?.product?.purchasePriceHT!! * product.product.quantity!!,
            tva = product.product.getTvaValue() * product.product.quantity,
            quantity = product.product.quantity,
            newQuantity = physicalQuantity,
            htDifference = (product.product.purchasePriceHT * product.product.quantity) - (physicalQuantity * product.product.purchasePriceHT),
            tvaDifference = (product.product.getTvaValue() * product.product.quantity) - (physicalQuantity * product.product.getTvaValue())
        ).apply {
            selectedProduct = product
        }

        inventory.value =
            inventory.value?.copy(
                productsCount = inventory.value?.productsCount!! + 1,
                htAmount = inventory.value?.htAmount!! + line.buyPriceHT,
                tvaAmount = inventory.value?.tvaAmount!! + line.tva,
                newHTAmount = inventory.value?.newHTAmount!! + line.htDifference,
                newTVAAmount = inventory.value?.newTVAAmount!! + line.tvaDifference,
                htDifference = inventory.value?.htDifference!! + line.htDifference,
                tvaDifference = inventory.value?.tvaDifference!! + line.tvaDifference,
            )

        return line
    }

    fun removeLine(line: InventoryLines) {
        inventory.value =
            inventory.value?.copy(
                productsCount = inventory.value?.productsCount!! - 1,
                htAmount = inventory.value?.htAmount!! - line.buyPriceHT,
                tvaAmount = inventory.value?.tvaAmount!! - line.tva,
                newHTAmount = inventory.value?.newHTAmount!! - line.htDifference,
                newTVAAmount = inventory.value?.newTVAAmount!! - line.tvaDifference,
                htDifference = inventory.value?.htDifference!! - line.htDifference,
                tvaDifference = inventory.value?.tvaDifference!! - line.tvaDifference,
            )

    }

    fun saveInventory(): LiveData<Resource<Long>> {
        val data = MutableLiveData<Resource<Long>>()

        enqueue(repository.insertInventory(inventory.value!!), data)
        return data
    }

    fun saveLines(lines: List<InventoryLines>): LiveData<Resource<List<Long>>> {
        val data = MutableLiveData<Resource<List<Long>>>()
        enqueue(repository.insertInventoryLines(*lines.toTypedArray()), data)
        return data
    }

}