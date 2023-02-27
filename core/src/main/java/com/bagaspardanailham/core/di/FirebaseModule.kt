package com.bagaspardanailham.core.di

import com.bagaspardanailham.core.data.repository.FirebaseRemoteConfigRepository
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalyticsRepository(firebaseAnalytics: FirebaseAnalytics): FirebaseAnalyticsRepository {
        return FirebaseAnalyticsRepository(firebaseAnalytics)
    }

}