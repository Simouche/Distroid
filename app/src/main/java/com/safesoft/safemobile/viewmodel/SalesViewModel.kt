package com.safesoft.safemobile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.safesoft.safemobile.backend.db.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.db.entity.SaleLines
import com.safesoft.safemobile.backend.db.entity.Sales
import com.safesoft.safemobile.backend.repository.SalesRepository
import com.safesoft.safemobile.backend.utils.getCurrentDateTime
import com.safesoft.safemobile.forms.SalesForm
import com.safesoft.safemobile.ui.products.ProductCalculator

class SalesViewModel @ViewModelInject constructor(
    private val salesRepository: SalesRepository,
    private val config: PagedList.Config,
    val salesForm: SalesForm
) : BaseViewModel(), ProductCalculator {

    val invoice = MutableLiveData<Sales>().apply {
        value = Sales(
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


    val salesList: LiveData<PagedList<Sales>> =
        salesRepository.getAllSales().toLiveData(config = config)

    fun search(query: String): LiveData<PagedList<Sales>> =
        salesRepository.searchSales(query).toLiveData(config = config)


    fun addLine(product: AllAboutAProduct?, quantity: Double): SaleLines {
        val line = SaleLines(
            id = 0,
            product = product?.product?.id ?: 0,
            quantity,
            quantity * (product?.product?.sellPriceDetailHT ?: 0.0),
            calculateNewPrice(
                quantity * (product?.product?.sellPriceDetailHT ?: 0.0),
                product?.product?.tva ?: 0.0
            ),
            calculatePercentage(
                quantity * (product?.product?.sellPriceDetailHT ?: 0.0),
                product?.product?.tva ?: 0.0
            ),
            sale = 0,
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

    fun removeLine(line: SaleLines) {
        invoice.value =
            invoice.value?.copy(
                totalHT = (invoice.value?.totalHT!! - line.totalBuyPriceHT),
                productsCount = invoice.value?.productsCount!! - 1,
                tva = invoice.value?.tva!! - line.tva!!,
                totalTTC = invoice.value?.totalTTC!! - line.totalBuyPriceTTC,
            )
    }

    fun saveInvoice(lines: List<SaleLines>) {

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