package com.bagaspardanailham.myecommerceapp.di

import com.bagaspardanailham.myecommerceapp.data.repository.FirebaseRemoteConfigRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideRemoteConfigRepository(frc: FirebaseRemoteConfig): FirebaseRemoteConfigRepository {
        return FirebaseRemoteConfigRepository(frc)
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return FirebaseRemoteConfig.getInstance()
    }

}