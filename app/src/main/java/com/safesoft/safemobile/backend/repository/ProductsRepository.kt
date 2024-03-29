package com.safesoft.safemobile.backend.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.safesoft.safemobile.backend.api.service.ProductService
import com.safesoft.safemobile.backend.db.local.dao.ProductsDao
import com.safesoft.safemobile.backend.db.local.entity.Barcodes
import com.safesoft.safemobile.backend.db.local.entity.Brands
import com.safesoft.safemobile.backend.db.local.entity.ProductWithBarcodes
import com.safesoft.safemobile.backend.db.local.entity.Products
import com.safesoft.safemobile.backend.db.remote.dao.RemoteBarCodeDao
import com.safesoft.safemobile.backend.db.remote.dao.RemoteProductDao
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsService: ProductService,
    private val remoteProductDao: RemoteProductDao,
    private val remoteBarCodeDao: RemoteBarCodeDao,
) {

    fun getAllProductsWithBarcodes() = productsDao.getAllProductsWithBarcodes()

    fun getProductMainBarcode(product: Long) = productsDao.getProductMainBarcode(product)

    fun getAllAboutAProductById(product: Long) = productsDao.getAllAboutAProductById(product)

    fun getAllAboutAProductByBarcode(barcode: Long) =
        productsDao.getAllAboutAProductByBarcode(barcode)

    fun getAllProductsWithBarCodesSingle() = productsDao.getAllProductsWithBarCodesSingle()

    fun getAllNewProductsWithBarCodes() = productsDao.getAllNewProductsWithBarCodes()

    fun getAllProductsAllInfo() = productsDao.getAllProductsWithAllInfo()

    fun getAllProductsWithExpirationDates() = productsDao.getAllProductsWithExpirationDates()

    fun getAllProductsWithBrands() = productsDao.getAllProductsWithBrands()

    fun getAllProductsWithProviders() = productsDao.getAllProductsWithProviders()

    fun searchProductByBarcode(barcode: String) = productsDao.searchProductByBarcode(barcode)

    fun searchProduct(query: String) = productsDao.searchProduct(query)

    fun searchProductFlow(query: String) = productsDao.searchProductFlow(query)

    fun getProductWithBarcodeById(id: Long) = productsDao.getProductWithBarcodeById(id)

    fun getProductWithProvidersById(id: Long) = productsDao.getProductWithProvidersById(id)

    fun addProducts(vararg products: Products) = productsDao.addProducts(*products)

    fun addProduct(product: Products) = productsDao.addProduct(product)

    fun getAllBrands(query: String) = productsDao.getAllBrands(query)

    fun addBrand(brand: Brands) = productsDao.addBrand(brand)

    fun getAllProductsFromServer() = productsService.getAllProducts()

    suspend fun addBarCodes(vararg barCodes: Barcodes) = productsDao.addBarCodeSuspend(*barCodes)

    suspend fun getLatestProduct() = productsDao.getLatestProduct()

    fun updateProducts(products: List<ProductWithBarcodes>) =
        productsService.updateProducts(products)

    fun insertProductsInRemoteDB(products: List<Products>) =
        remoteProductDao.insert(*products.toTypedArray())

    fun insertProductBarCodesInRemoteDB(barcodes: List<Barcodes>) =
        remoteBarCodeDao.insert(*barcodes.toTypedArray())

    fun loadProductsFromRemoteDB() = remoteProductDao.select()

    fun loadProductBarCodesFromRemoteDB(
        specialSelect: String = "",
        where: String = "",
        whereArgs: Map<Int, Any> = mapOf()
    ) = remoteBarCodeDao.select(
        specialSelect = specialSelect,
        where = where,
        whereArgs = whereArgs
    )

    fun loadStock() = remoteProductDao.getStock()

    fun setProductStock(newStock: Double, barcode: String) =
        productsDao.setProductStock(newStock, barcode)

    fun markProductsAsSynched() = productsDao.markProductsAsSynched()

}