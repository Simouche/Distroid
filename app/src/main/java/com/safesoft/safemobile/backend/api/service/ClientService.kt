package com.safesoft.safemobile.backend.api.service

import com.safesoft.safemobile.backend.api.response.ClientsResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface ClientService {
    @GET("get-all-clients")
    fun getAllClients(): Flowable<ClientsResponse>

    @GET("synchronize-clients")
    fun synchronizeClients(@Query("ids") ids: Array<Int>): Flowable<ClientsResponse>
}