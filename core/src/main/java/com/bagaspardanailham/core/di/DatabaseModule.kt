package com.bagaspardanailham.core.di

import android.content.Context
import com.bagaspardanailham.core.data.local.room.EcommerceDatabase
import com.bagaspardanailham.core.data.local.room.NotificationDao
import com.bagaspardanailham.core.data.remote.ApiService
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.core.data.repository.EcommerceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideEcommerceDatabase(@ApplicationContext context: Context): EcommerceDatabase =
        EcommerceDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideNotificationDao(ecommerceDatabase: EcommerceDatabase): NotificationDao {
        return ecommerceDatabase.notificationDao()
    }

    @Provides
    fun provideRepository(apiService: ApiService, ecommerceDatabase: EcommerceDatabase): EcommerceRepository = EcommerceRepositoryImpl(apiService, ecommerceDatabase)

}