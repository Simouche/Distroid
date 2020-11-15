package com.safesoft.safemobile.backend.di.module

import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.safesoft.safemobile.BuildConfig
import com.safesoft.safemobile.backend.di.UnAuthInterceptorOkHttpClient
import com.safesoft.safemobile.backend.di.UnAuthRetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(): Cache {
        return Cache(File(""), 2000);
    }

    @Provides
    @Singleton
    @UnAuthInterceptorOkHttpClient
    fun provideOkHttpClient(logger: HttpLoggingInterceptor, cache: Cache): OkHttpClient {
        val client = OkHttpClient().newBuilder()
            .cache(cache)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG)
            client.addInterceptor(interceptor = logger)
        return client.build()
    }

    @Provides
    @Singleton
    @UnAuthRetrofitClient
    fun provideRetrofitClient(
        @UnAuthInterceptorOkHttpClient okHttpClient: OkHttpClient,
        preferences: SharedPreferences
    ): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(preferences.getString("base_url", "https//:127.0.0.1:8080/")!!)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .enableComplexMapKeySerialization()
                        .serializeNulls()
                        .setPrettyPrinting()
                        .create()
                )
            ).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }
}