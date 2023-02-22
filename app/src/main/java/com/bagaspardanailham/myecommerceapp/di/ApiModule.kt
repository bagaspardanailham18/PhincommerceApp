package com.bagaspardanailham.myecommerceapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.viewbinding.BuildConfig
import com.bagaspardanailham.myecommerceapp.data.remote.*
import com.bagaspardanailham.myecommerceapp.network.NetworkErrorInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, authAuthenticator: AuthAuthenticator, authErrorInterceptor: AuthErrorInterceptor, networkErrorInterceptor: NetworkErrorInterceptor): OkHttpClient {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(authErrorInterceptor)
            .addInterceptor(networkErrorInterceptor)
            .authenticator(authAuthenticator)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor = AuthInterceptor(tokenManager)

    @Singleton
    @Provides
    fun provideAuthAuthenticator(tokenManager: TokenManager): AuthAuthenticator = AuthAuthenticator(tokenManager)

    @Singleton
    @Provides
    fun provideAuthErrorInterceptor(tokenManager: TokenManager, @ApplicationContext context: Context): AuthErrorInterceptor = AuthErrorInterceptor(tokenManager, context)

    @Singleton
    @Provides
    fun provideNetworkErrorInterceptor(@ApplicationContext context: Context): NetworkErrorInterceptor = NetworkErrorInterceptor(context)

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://portlan.id/training_android/public/")
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient, retrofitBuilder: Retrofit.Builder): ApiService {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenManager(pref: DataStore<Preferences>): TokenManager = TokenManager(pref)

//    @Provides
//    @Singleton
//    fun provideApiService(): ApiService = ApiConfig.getApiService()

//    fun providesAuthRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .baseUrl("http://localhost:3001/training_android/public/")
//            .build()
//    }

}