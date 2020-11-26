package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.api.service.ProductService
import com.safesoft.safemobile.backend.db.dao.ProductsDao
import com.safesoft.safemobile.backend.db.entity.Barcodes
import com.safesoft.safemobile.backend.db.entity.Brands
import com.safesoft.safemobile.backend.db.entity.Products
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsService: ProductService
) {

    fun getAllProductsWithBarcodes() = productsDao.getAllProductsWithBarcodes()

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

    fun getAllBrands(query: String) = productsDao.getAllBrands(query)

    fun addBrand(brand: Brands) = productsDao.addBrand(brand)

    fun getAllProductsFromServer() = productsService.getAllProducts()

    suspend fun addBarCodes(vararg barCodes: Barcodes) = productsDao.addBarCodeSuspend(*barCodes)

    suspend fun getLatestProduct() = productsDao.getLatestProduct()

    fun updateProducts(products: List<Products>) = productsService.updateProducts(products)

}