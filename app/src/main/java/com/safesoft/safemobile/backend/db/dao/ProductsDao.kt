package com.safesoft.safemobile.backend.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.safesoft.safemobile.backend.db.entity.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface ProductsDao {
    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProductsWithBarcodes(): DataSource.Factory<Int, ProductWithBarcodes>

    @Transaction
    @Query("SELECT * FROM products JOIN barcodes ON products.PRODUCT_ID = barcodes.PRODUCT WHERE barcodes.CODE LIKE '%'||:barcode||'%'")
    fun searchProductByBarcode(barcode: String): DataSource.Factory<Int, AllAboutAProduct>

    @Transaction
    @Query("SELECT * FROM products LEFT JOIN barcodes ON products.PRODUCT_ID = barcodes.PRODUCT LEFT JOIN brands ON products.BRAND = brands.id LEFT JOIN expiration_dates ON products.PRODUCT_ID = expiration_dates.PRODUCT WHERE products.REFERENCE like '%'||:query||'%' OR products.DESIGNATION like '%'||:query||'%' OR barcodes.CODE LIKE '%'||:query||'%' OR brands.NAME LIKE '%'||:query||'%'")
    fun searchProduct(query: String): DataSource.Factory<Int, AllAboutAProduct>

    @Transaction
    @Query("SELECT DISTINCT * FROM products LEFT JOIN barcodes ON products.PRODUCT_ID = barcodes.PRODUCT LEFT JOIN brands ON products.BRAND = brands.id LEFT JOIN expiration_dates ON products.PRODUCT_ID = expiration_dates.PRODUCT WHERE products.REFERENCE like '%'||:query||'%' OR products.DESIGNATION like '%'||:query||'%' OR barcodes.CODE LIKE '%'||:query||'%' OR brands.NAME LIKE '%'||:query||'%' ORDER BY products.PRODUCT_ID LIMIT 50")
    fun searchProductFlow(query: String): Flowable<List<AllAboutAProduct>>

    @Transaction
    @Query("SELECT * FROM products WHERE PRODUCT_ID=:id")
    fun getProductWithBarcodeById(id: Long): Single<ProductWithBarcodes>

    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProductsWithExpirationDates(): DataSource.Factory<Int, ProductWithExpirationDates>

    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProductsWithAllInfo(): DataSource.Factory<Int, AllAboutAProduct>

    @Transaction
    @Query("SELECT * FROM brands")
    fun getAllProductsWithBrands(): DataSource.Factory<Int, BrandWithProducts>

    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProductsWithProviders(): DataSource.Factory<Int, ProductWithProviders>

    @Transaction
    @Query("SELECT * FROM products WHERE PRODUCT_ID=:id")
    fun getProductWithProvidersById(id: Long): Single<ProductWithProviders>

    @Query("SELECT * FROM products ORDER BY PRODUCT_ID DESC LIMIT 1")
    suspend fun getLatestProduct(): Products

    @Query("SELECT * FROM brands WHERE NAME LIKE '%'||:query||'%'")
    fun getAllBrands(query: String): Flowable<List<Brands>>

    @Insert
    fun addProducts(vararg providers: Products): Completable

    @Update
    fun updateProducts(vararg providers: Products): Completable

    @Delete
    fun deleteProducts(vararg providers: Products): Completable

    @Insert
    fun addBrand(vararg brands: Brands): Completable

    @Update
    fun updateBrand(vararg brands: Brands): Completable

    @Delete
    fun deleteBrand(vararg brands: Brands): Completable

    @Insert
    fun addBarCode(vararg barCodes: Barcodes): Completable

    @Update
    fun updateBarCode(vararg barCodes: Barcodes): Completable

    @Delete
    fun deleteBarCode(vararg barCodes: Barcodes): Completable

    @Insert
    suspend fun addBarCodeSuspend(vararg barCodes: Barcodes)

    @Update
    suspend fun updateBarCodeSuspend(vararg barCodes: Barcodes)

    @Delete
    suspend fun deleteBarCodeSuspend(vararg barCodes: Barcodes)


}