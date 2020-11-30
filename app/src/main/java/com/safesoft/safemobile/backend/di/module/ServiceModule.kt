package com.safesoft.safemobile.backend.di.module

import com.safesoft.safemobile.backend.api.service.ClientService
import com.safesoft.safemobile.backend.api.service.ProductService
import com.safesoft.safemobile.backend.api.service.ProviderService
import com.safesoft.safemobile.backend.api.service.PurchaseService
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

    @Provides
    fun provideProviderService(@UnAuthRetrofitClient retrofit: Retrofit): ProviderService =
        retrofit.create(ProviderService::class.java)

    @Provides
    fun provideProductsService(@UnAuthRetrofitClient retrofit: Retrofit): ProductService =
        retrofit.create(ProductService::class.java)

    @Provides
    fun providerPurchasesService(@UnAuthRetrofitClient retrofit: Retrofit): PurchaseService =
        retrofit.create(PurchaseService::class.java)
}