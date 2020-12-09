package com.safesoft.safemobile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.safesoft.safemobile.backend.db.local.entity.AllAboutAProduct
import com.safesoft.safemobile.backend.db.local.entity.PurchaseLines
import com.safesoft.safemobile.backend.db.local.entity.Purchases
import com.safesoft.safemobile.backend.repository.PurchasesRepository
import com.safesoft.safemobile.backend.utils.Resource
import com.safesoft.safemobile.forms.PurchaseForm
import com.safesoft.safemobile.ui.products.ProductCalculator
import java.util.*


class PurchasesViewModel @ViewModelInject constructor(
    private val purchasesRepository: PurchasesRepository,
    private val config: PagedList.Config,
    val purchaseForm: PurchaseForm
) : BaseViewModel(), ProductCalculator,BaseFormOwner {

    var paymentType: String = "C"
        set(value) {
            invoice.value =
                invoice.value?.copy(reglement = value)
        }

    var provider: Long = 0
        set(value) {
            invoice.value = invoice.value!!.copy(provider = value)
        }

    val invoice = MutableLiveData<Purchases>().apply {
        value = Purchases(
            0,
            "",
            "",
            Date(),
            0,
            0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            "C",
            0.0,
            "",
            false,
            0.0
        )
    }

    val purchasesList: LiveData<PagedList<Purchases>> =
        purchasesRepository.getAllPurchases().toLiveData(config = config)

    fun search(query: String): LiveData<PagedList<Purchases>> =
        purchasesRepository.searchPurchases(query).toLiveData(config = config)

    fun addLine(product: AllAboutAProduct?, quantity: Double): PurchaseLines {
        val line = PurchaseLines(
            id = 0,
            product = product?.product?.id ?: 0,
            quantity = quantity,
            totalBuyPriceHT = quantity * (product?.product?.purchasePriceHT ?: 0.0),
            totalBuyPriceTTC = calculateNewPrice(
                quantity * (product?.product?.purchasePriceHT ?: 0.0),
                product?.product?.tva ?: 0.0
            ),
            tva = calculatePercentage(
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
                tva = invoice.value?.tva!! + line.tva,
                totalTTC = invoice.value?.totalTTC!! + line.totalBuyPriceTTC,
            )

        return line
    }

    fun removeLine(line: PurchaseLines) {
        invoice.value =
            invoice.value?.copy(
                totalHT = (invoice.value?.totalHT!! - line.totalBuyPriceHT),
                productsCount = invoice.value?.productsCount!! - 1,
                tva = invoice.value?.tva!! - line.tva,
                totalTTC = invoice.value?.totalTTC!! - line.totalBuyPriceTTC,
            )
    }

    fun saveInvoice(): LiveData<Resource<Long>> {
        val data = MutableLiveData<Resource<Long>>()

        enqueue(purchasesRepository.insertNewPurchase(invoice.value!!), data)
        return data
    }

    fun saveLines(lines: List<PurchaseLines>): LiveData<Resource<List<Long>>> {
        val data = MutableLiveData<Resource<List<Long>>>()
        enqueue(purchasesRepository.insertPurchaseLines(*lines.toTypedArray()), data)
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

    override fun reInitFields() {
        val fields = purchaseForm.fields
        fields.date.value = ""
        fields.done.value = false
        fields.purchaseNumber.value = ""
        fields.invoiceNumber.value = ""
        fields.provider.value = 0
        fields.providerName.value = ""
        fields.productsCount.value = 0
        fields.totalHT.value = 0.0
        fields.totalTTC.value = 0.0
        fields.tva.value = 0.0
        fields.stamp.value = 0.0
        fields.reglement.value = ""
        fields.totalQuantity.value =0.0
        fields.note.value = ""
        fields.payment.value = 0.0
    }

}