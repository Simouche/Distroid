package com.safesoft.safemobile.backend.api.service

import com.safesoft.safemobile.backend.api.response.ProvidersResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ProviderService {

    @GET("get-all-providers")
    fun getAllProviders(): Single<ProvidersResponse>

    @GET("synchronize-providers")
    fun synchronizeProviders(@Query("ids") ids: Array<Int>): Single<ProvidersResponse>
}