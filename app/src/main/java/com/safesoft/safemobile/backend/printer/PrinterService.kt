package com.safesoft.safemobile.backend.printer

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.safesoft.safemobile.R
import com.safesoft.safemobile.backend.db.local.entity.AllAboutASale
import com.safesoft.safemobile.backend.db.local.entity.Sales
import com.safesoft.safemobile.backend.repository.PreferencesRepository
import com.safesoft.safemobile.backend.repository.ProductsRepository
import com.safesoft.safemobile.backend.repository.PurchasesRepository
import com.safesoft.safemobile.backend.repository.SalesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class PrinterService : Service() {
    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var salesRepository: SalesRepository

    @Inject
    lateinit var purchasesRepository: PurchasesRepository

    @Inject
    lateinit var productsRepository: ProductsRepository

    private val printerDpi = 203
    private val printerWidthMM = 54f
    private val printerNbrCharactersPerLine = 32

    private val binder = PrinterBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class PrinterBinder : Binder() {
        val service: PrinterService
            get() = this@PrinterService
    }

    private fun appendHeader(content: String): String {
        if (!preferencesRepository.getShowHeader()) return content
        var nContent = content
        nContent =
            "[C]<font size='big'>${preferencesRepository.getEnterpriseAddress()}</font>\n$nContent"
        nContent =
            "[C]<font size='big'>${preferencesRepository.getEnterprisePhone()}</font>\n$nContent"
        nContent =
            "[C]<font size='big'>${preferencesRepository.getEnterpriseName()}</font>\n$nContent"
        return nContent
    }

    private fun appendFooter(content: String): String {
        if (!preferencesRepository.getShowHeader()) return content
        var nContent = content
        nContent = "$content\n${preferencesRepository.getEnterpriseFooter()}"
        return nContent
    }

    fun printPurchase(purchaseId: Long) {

    }

    fun printSale(saleId: Long) {
        GlobalScope.launch {
            val sale = salesRepository.getSaleWithAllInfoById(saleId).blockingGet()
            prepareSaleLinesProducts(sale)
            val printer = EscPosPrinter(
                BluetoothPrintersConnections.selectFirstPaired(),
                printerDpi,
                printerWidthMM,
                printerNbrCharactersPerLine
            )
            var printable = sale.getPrintable()
            printable = appendHeader(printable)
            printable = appendFooter(printable)
            Log.d("Service", "printSale: printing $printable")
            printer.printFormattedText(printable)
            printer.disconnectPrinter()
        }
    }

    private fun prepareSaleLinesProducts(sale: AllAboutASale) {
        for (i in sale.saleLines.indices)
            sale.saleLines[i].selectedProduct =
                productsRepository.getAllAboutAProductById(sale.saleLines[i].product).blockingGet()
    }

    fun print() {
        val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
        printer
            .printFormattedText(
                """
        [C]<img>${
                    PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        resources
                            .getDrawableForDensity(R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM)
                    )
                }</img>
        [L]
        [C]<u><font size='big'>ORDER N°045</font></u>
        [L]
        [C]================================
        [L]
        [L]<b>BEAUTIFUL SHIRT</b>[R]9.99e
        [L]  + Size : S
        [L]
        [L]<b>AWESOME HAT</b>[R]24.99e
        [L]  + Size : 57/58
        [L]
        [C]--------------------------------
        [R]TOTAL PRICE :[R]34.98e
        [R]TAX :[R]4.23e
        [L]
        [C]================================
        [L]
        [L]<font size='tall'>Customer :</font>
        [L]Raymond DUPONT
        [L]5 rue des girafes
        [L]31547 PERPETES
        [L]Tel : +33801201456
        [L]
        [C]<barcode type='ean13' height='10'>831254784551</barcode>
        [C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>
        """.trimIndent() + "\n \n \n \n"
            )
    }
}
