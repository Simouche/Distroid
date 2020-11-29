package com.safesoft.safemobile.backend.api.service

import com.safesoft.safemobile.backend.api.response.BaseResponse
import com.safesoft.safemobile.backend.api.response.ProductsResponse
import com.safesoft.safemobile.backend.db.entity.ProductWithBarcodes
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductService {
    @GET("get-all-products")
    fun getAllProducts(): Single<ProductsResponse>

    @GET("synchronize-products")
    fun synchronizeProducts(@Query("ids") ids: Array<Int>): Single<ProductsResponse>

    @POST("add-products")
    fun updateProducts(@Body products: List<ProductWithBarcodes>): Single<BaseResponse>
}