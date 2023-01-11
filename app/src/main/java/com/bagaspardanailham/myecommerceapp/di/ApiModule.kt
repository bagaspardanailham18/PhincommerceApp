package com.bagaspardanailham.myecommerceapp.di

import com.bagaspardanailham.myecommerceapp.data.remote.ApiConfig
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService = ApiConfig.getApiService()

}