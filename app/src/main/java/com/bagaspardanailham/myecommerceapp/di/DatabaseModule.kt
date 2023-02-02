package com.bagaspardanailham.myecommerceapp.di

import android.content.Context
import com.bagaspardanailham.myecommerceapp.data.local.room.EcommerceDatabase
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

}