package com.bagaspardanailham.myecommerceapp.di

import android.content.Context
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.room.EcommerceDatabase
import com.bagaspardanailham.myecommerceapp.data.local.room.NotificationDao
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import com.bagaspardanailham.myecommerceapp.ui.notification.NotificationViewModel
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
    fun provideRepository(apiService: ApiService, ecommerceDatabase: EcommerceDatabase): EcommerceRepository = EcommerceRepository(apiService, ecommerceDatabase)

    @Provides
    @Singleton
    fun provideNotificationViewModel(ecommerceRepository: EcommerceRepository): NotificationViewModel = NotificationViewModel(ecommerceRepository)

}