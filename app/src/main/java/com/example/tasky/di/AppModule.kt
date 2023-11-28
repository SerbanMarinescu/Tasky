package com.example.tasky.di

import android.content.Context
import android.content.SharedPreferences
import com.example.tasky.TaskyApp
import com.example.tasky.common.Constants
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.util.ApiKeyInterceptor
import com.example.tasky.util.ApiTokenInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskyApp(): TaskyApp {
        return TaskyApp()
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("Prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(userPrefs: UserPreferences): OkHttpClient {
        return  OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(Constants.API_KEY))
            .addInterceptor(ApiTokenInterceptor(userPrefs))
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}