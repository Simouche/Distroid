package com.safesoft.safemobile.backend.api.service

import com.safesoft.safemobile.backend.api.response.BaseResponse
import com.safesoft.safemobile.backend.api.wrapper.UpdatePurchaseWrapper
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchaseService {

    @POST("add-purchases")
    fun updatePurchases(@Body purchases: UpdatePurchaseWrapper): Single<BaseResponse>
}