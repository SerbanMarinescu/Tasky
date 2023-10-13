package com.example.tasky.di

import android.content.Context
import android.content.SharedPreferences
import com.example.tasky.TaskyApp
import com.example.tasky.common.Constants
import com.example.tasky.util.ApiKeyInterceptor
import com.example.tasky.util.ApiTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(app: TaskyApp): SharedPreferences {
        return app.getSharedPreferences("Prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(prefs: SharedPreferences): OkHttpClient {
        return  OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(Constants.API_KEY))
            .addInterceptor(ApiTokenInterceptor(prefs.getString("jwt", null)))
            .build()
    }
}