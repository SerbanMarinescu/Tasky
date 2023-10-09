package com.example.tasky.feature_agenda.di

import androidx.room.Room
import com.example.tasky.TaskyApp
import com.example.tasky.common.Constants
import com.example.tasky.common.Constants.DATABASE_NAME
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.local.Daos
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AgendaModule {

    @Provides
    @Singleton
    fun provideAgendaApi(client: OkHttpClient): TaskyAgendaApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAgendaDatabase(app: TaskyApp) : AgendaDatabase {
        return Room.databaseBuilder(
            app,
            AgendaDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providDaos(db: AgendaDatabase): Daos {
        return Daos(
            eventDao = db.eventDao,
            taskDao = db.taskDao,
            reminderDao = db.reminderDao,
        )
    }
}