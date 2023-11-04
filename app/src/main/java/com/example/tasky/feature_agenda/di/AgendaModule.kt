package com.example.tasky.feature_agenda.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.tasky.TaskyApp
import com.example.tasky.common.Constants
import com.example.tasky.common.Constants.DATABASE_NAME
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.repository.AgendaRepositoryImpl
import com.example.tasky.feature_agenda.data.repository.EventRepositoryImpl
import com.example.tasky.feature_agenda.data.repository.ReminderRepositoryImpl
import com.example.tasky.feature_agenda.data.repository.TaskRepositoryImpl
import com.example.tasky.feature_agenda.data.util.MoshiSerializer
import com.example.tasky.feature_agenda.data.util.TaskSchedulerImpl
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.util.JsonSerializer
import com.example.tasky.feature_agenda.domain.util.TaskScheduler
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideRepositories(
        api: TaskyAgendaApi,
        db: AgendaDatabase,
        userPrefs: UserPreferences
    ): AgendaRepositories {
        return AgendaRepositories(
            agendaRepository = AgendaRepositoryImpl(api, db),
            eventRepository = EventRepositoryImpl(api, db, userPrefs),
            reminderRepository = ReminderRepositoryImpl(api, db),
            taskRepository = TaskRepositoryImpl(api, db)
        )
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideJsonSerializer(): JsonSerializer {
        return MoshiSerializer()
    }

    @Provides
    @Singleton
    fun provideTaskScheduler(db: AgendaDatabase): TaskScheduler {
        return TaskSchedulerImpl(db)
    }
}