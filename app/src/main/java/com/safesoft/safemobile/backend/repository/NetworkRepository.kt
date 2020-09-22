package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.di.AuthRetrofitClient
import com.safesoft.safemobile.backend.di.UnAuthRetrofitClient
import retrofit2.Retrofit
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    @UnAuthRetrofitClient unAuthRetrofitClient: Retrofit,
    @AuthRetrofitClient authRetrofitClient: Retrofit
) {
}