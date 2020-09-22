package com.safesoft.safemobile.backend.di.module


import android.content.SharedPreferences
import androidx.paging.Config
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PagingModule {

    @Provides
    @Singleton
    fun providePagingConfig(preferences: SharedPreferences) =
        Config(
            pageSize = preferences.getInt("page_size", 25),
            prefetchDistance = preferences.getInt("prefetch_distance", 75),
            enablePlaceholders = preferences.getBoolean("enable_placeholders", true)
        )
}