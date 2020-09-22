package com.safesoft.safemobile.backend.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnAuthInterceptorOkHttpClient


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofitClient


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnAuthRetrofitClient

