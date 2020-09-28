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
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.backend.utils.getCurrentDateTime
import com.safesoft.safemobile.forms.SalesForm
import com.safesoft.safemobile.ui.products.ProductCalculator

/**
 * Sales ViewModel
 * Works with @see com.safesoft.safemobile.ui.sales.SalesListFragment and com.safesoft.safemobile.ui.sales.CreateSaleFragment
 * Process of calculations:
 * Each product has 3 prices,
 */
class SalesViewModel @ViewModelInject constructor(
    private val salesRepository: SalesRepository,
    private val config: PagedList.Config,
    val salesForm: SalesForm
) : BaseViewModel(), ProductCalculator {

    var paymentType: String = "C"
        set(value) {
            invoice.value =
                invoice.value?.copy(reglement = value)
        }

    var client: Long = 0
        set(value) {
            invoice.value = invoice.value!!.copy(client = value)
        }


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
            totalSellPriceHT = product?.product?.calculateSellPriceDOnHT(quantity) ?: 0.0,
            totalSellPriceTTC = calculateNewPrice(
                (product?.product?.calculateSellPriceDOnHT(quantity) ?: 0.0),
                product?.product?.tva ?: 0.0
            ),
            tva = calculatePercentage(
                quantity * (product?.product?.sellPriceDetailHT ?: 0.0),
                product?.product?.tva ?: 0.0
            ),
            discount = product?.product?.getDiscountAmountOnDHT() ?: 0.0,
            sale = 0,
            note = null,
        ).apply { selectedProduct = product }

        invoice.value =
            invoice.value?.copy(
                totalHT = (invoice.value?.totalHT!! + line.totalSellPriceHT),
                productsCount = invoice.value?.productsCount!! + 1,
                tva = invoice.value?.tva!! + line.tva!!,
                totalTTC = invoice.value?.totalTTC!! + line.totalSellPriceTTC,
                discount = invoice.value!!.discount!! + line.discount
            )

        return line
    }

    fun removeLine(line: SaleLines) {
        invoice.value =
            invoice.value?.copy(
                totalHT = (invoice.value?.totalHT!! - line.totalSellPriceHT),
                productsCount = invoice.value?.productsCount!! - 1,
                tva = invoice.value?.tva!! - line.tva!!,
                totalTTC = invoice.value?.totalTTC!! - line.totalSellPriceTTC,
                discount = invoice.value!!.discount!! - line.discount
            )
    }


    fun saveInvoice(): LiveData<Resource<Long>> {
        val data = MutableLiveData<Resource<Long>>()
        enqueue(salesRepository.insertNewSale(invoice.value!!), data)
        return data
    }

    fun saveLines(lines: List<SaleLines>): LiveData<Resource<List<Long>>> {
        val data = MutableLiveData<Resource<List<Long>>>()
        enqueue(salesRepository.insertSaleLines(*lines.toTypedArray()), data)
        return data
    }

    fun setStamp() {
        invoice.value =
            invoice.value?.copy(
                stamp = calculateStamp(
                    ((invoice.value?.totalHT ?: 0.0) + (invoice.value?.tva ?: 0.0))
                ),
                totalTTC = calculateStamp(
                    ((invoice.value?.totalHT ?: 0.0) + (invoice.value?.tva ?: 0.0)),
                ) + invoice.value!!.totalTTC!!
            )
    }

    fun removeStamp() {
        invoice.value =
            invoice.value?.copy(
                totalTTC = invoice.value!!.totalTTC!! - invoice.value!!.stamp!!,
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

    fun setPayment(amount: Double) {
        invoice.value =
            invoice.value?.copy(payment = amount, done = amount == invoice.value!!.totalTTC)
    }
}