package com.grupo1.deremate.di

import com.grupo1.deremate.infrastructure.ApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient {
        return ApiClient().setBaseUrl("http://10.0.2.2:8080")
    }
}
