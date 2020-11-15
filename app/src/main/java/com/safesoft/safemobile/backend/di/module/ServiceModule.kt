package com.safesoft.safemobile.backend.di.module

import com.safesoft.safemobile.backend.api.service.ClientService
import com.safesoft.safemobile.backend.di.UnAuthRetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit

@Module
@InstallIn(ApplicationComponent::class)
class ServiceModule {

    @Provides
    fun provideClientService(@UnAuthRetrofitClient retrofit: Retrofit): ClientService =
        retrofit.create(ClientService::class.java)

}