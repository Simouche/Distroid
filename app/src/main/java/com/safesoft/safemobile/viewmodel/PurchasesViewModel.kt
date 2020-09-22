package com.safesoft.safemobile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.safesoft.safemobile.backend.db.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.db.entity.PurchaseLines
import com.safesoft.safemobile.backend.db.entity.Purchases
import com.safesoft.safemobile.backend.repository.PurchasesRepository
import com.safesoft.safemobile.backend.utils.getCurrentDateTime
import com.safesoft.safemobile.forms.PurchaseForm
import com.safesoft.safemobile.ui.products.ProductCalculator


class PurchasesViewModel @ViewModelInject constructor(
    private val purchasesRepository: PurchasesRepository,
    private val config: PagedList.Config,
    val purchaseForm: PurchaseForm
) : BaseViewModel(), ProductCalculator {


    val invoice = MutableLiveData<Purchases>().apply {
        value = Purchases(
            0,
            "",
            "",
            getCurrentDateTime(),
            0,
            0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            "E",
            0.0,
            "",
            false,
            0.0
        )
    };

    val purchasesList: LiveData<PagedList<Purchases>> =
        purchasesRepository.getAllPurchases().toLiveData(config = config)

    fun search(query: String): LiveData<PagedList<Purchases>> =
        purchasesRepository.searchPurchases(query).toLiveData(config = config)

    fun addLine(product: AllAboutAProduct?, quantity: Double): PurchaseLines {
        val line = PurchaseLines(
            id = 0,
            product = product?.product?.id ?: 0,
            quantity,
            quantity * (product?.product?.purchasePriceHT ?: 0.0),
            calculateNewPrice(
                quantity * (product?.product?.purchasePriceHT ?: 0.0),
                product?.product?.tva ?: 0.0
            ),
            calculatePercentage(
                quantity * (product?.product?.purchasePriceHT ?: 0.0),
                product?.product?.tva ?: 0.0
            ),
            purchase = 0,
            note = null,
        ).apply { selectedProduct = product }

        invoice.value =
            invoice.value?.copy(
                totalHT = (invoice.value?.totalHT!! + line.totalBuyPriceHT),
                productsCount = invoice.value?.productsCount!! + 1,
                tva = invoice.value?.tva!! + line.tva!!,
                totalTTC = invoice.value?.totalTTC!! + line.totalBuyPriceTTC,
            )

        return line
    }

    fun removeLine(line: PurchaseLines) {
        invoice.value =
            invoice.value?.copy(
                totalHT = (invoice.value?.totalHT!! - line.totalBuyPriceHT),
                productsCount = invoice.value?.productsCount!! - 1,
                tva = invoice.value?.tva!! - line.tva!!,
                totalTTC = invoice.value?.totalTTC!! - line.totalBuyPriceTTC,
            )
    }

    fun saveInvoice(lines: List<PurchaseLines>) {

    }

    fun setStamp() {
        invoice.value =
            invoice.value?.copy(
                stamp = calculateNewPrice(
                    invoice.value?.totalHT ?: 0.0,
                    1.0
                )
            )
    }

    fun removeStamp() {
        invoice.value =
            invoice.value?.copy(
                stamp = 0.0
            )
    }

    fun discount(discount: Double) {
        invoice.value = invoice.value?.copy(
            discount = calculatePercentage(
                invoice.value?.totalHT ?: 0.0,
                discount
            ),
//            totalTTC = calculateNewPrice(invoice.value?.total)
        )
    }

}