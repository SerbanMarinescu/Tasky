package com.example.tasky.feature_agenda.di

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import com.example.tasky.common.Constants
import com.example.tasky.common.Constants.DATABASE_NAME
import com.example.tasky.feature_agenda.data.local.AgendaDatabase
import com.example.tasky.feature_agenda.data.remote.TaskyAgendaApi
import com.example.tasky.feature_agenda.data.repository.AgendaRepositoryImpl
import com.example.tasky.feature_agenda.data.repository.EventRepositoryImpl
import com.example.tasky.feature_agenda.data.repository.ReminderRepositoryImpl
import com.example.tasky.feature_agenda.data.repository.TaskRepositoryImpl
import com.example.tasky.feature_agenda.data.util.MoshiSerializer
import com.example.tasky.feature_agenda.data.util.NotificationSchedulerImpl
import com.example.tasky.feature_agenda.data.util.PhotoValidatorImpl
import com.example.tasky.feature_agenda.data.util.TaskSchedulerImpl
import com.example.tasky.feature_agenda.domain.repository.AgendaRepositories
import com.example.tasky.feature_agenda.domain.use_case.AgendaUseCases
import com.example.tasky.feature_agenda.domain.use_case.CreateEvent
import com.example.tasky.feature_agenda.domain.use_case.CreateReminder
import com.example.tasky.feature_agenda.domain.use_case.CreateTask
import com.example.tasky.feature_agenda.domain.use_case.DeleteEvent
import com.example.tasky.feature_agenda.domain.use_case.DeleteReminder
import com.example.tasky.feature_agenda.domain.use_case.DeleteTask
import com.example.tasky.feature_agenda.domain.use_case.Event
import com.example.tasky.feature_agenda.domain.use_case.Logout
import com.example.tasky.feature_agenda.domain.use_case.Reminder
import com.example.tasky.feature_agenda.domain.use_case.Task
import com.example.tasky.feature_agenda.domain.use_case.UpdateEvent
import com.example.tasky.feature_agenda.domain.use_case.UpdateReminder
import com.example.tasky.feature_agenda.domain.use_case.UpdateTask
import com.example.tasky.feature_agenda.domain.use_case.ValidateAttendee
import com.example.tasky.feature_agenda.domain.use_case.ValidatePhotos
import com.example.tasky.feature_agenda.domain.util.JsonSerializer
import com.example.tasky.feature_agenda.domain.util.NotificationScheduler
import com.example.tasky.feature_agenda.domain.util.PhotoValidator
import com.example.tasky.feature_agenda.domain.util.TaskScheduler
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import com.squareup.moshi.Moshi
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
    fun provideAgendaApi(
        client: OkHttpClient,
        moshi: Moshi
    ): TaskyAgendaApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAgendaDatabase(@ApplicationContext context: Context) : AgendaDatabase {
        return Room.databaseBuilder(
            context,
            AgendaDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepositories(
        api: TaskyAgendaApi,
        db: AgendaDatabase,
        userPrefs: UserPreferences,
        photoValidator: PhotoValidator,
        jsonSerializer: JsonSerializer
    ): AgendaRepositories {
        return AgendaRepositories(
            agendaRepository = AgendaRepositoryImpl(api, db),
            eventRepository = EventRepositoryImpl(api, db, userPrefs, photoValidator, jsonSerializer),
            reminderRepository = ReminderRepositoryImpl(api, db),
            taskRepository = TaskRepositoryImpl(api, db)
        )
    }

    @Provides
    @Singleton
    fun provideJsonSerializer(moshi: Moshi): JsonSerializer {
        return MoshiSerializer(moshi)
    }

    @Provides
    @Singleton
    fun provideTaskScheduler(db: AgendaDatabase): TaskScheduler {
        return TaskSchedulerImpl(db)
    }

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(AlarmManager::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationScheduler(
       @ApplicationContext context: Context,
       alarmManager: AlarmManager
    ): NotificationScheduler {
        return NotificationSchedulerImpl(
            context,
            alarmManager
        )
    }

    @Provides
    @Singleton
    fun providePhotoValidator(@ApplicationContext context: Context): PhotoValidator {
        return PhotoValidatorImpl(context)
    }

    @Provides
    @Singleton
    fun provideEventUseCases(
        repositories: AgendaRepositories,
        taskScheduler: TaskScheduler,
        userDataValidator: UserDataValidator,
        photoValidator: PhotoValidator
    ): Event {
        return Event(
            createEvent = CreateEvent(repositories.eventRepository, taskScheduler),
            updateEvent = UpdateEvent(repositories.eventRepository, taskScheduler),
            deleteEvent = DeleteEvent(repositories.eventRepository, taskScheduler),
            validateAttendee = ValidateAttendee(userDataValidator, repositories.eventRepository),
            validatePhotos = ValidatePhotos(photoValidator)
        )
    }

    @Provides
    @Singleton
    fun provideReminderUseCases(
        repositories: AgendaRepositories,
        taskScheduler: TaskScheduler
    ): Reminder {
        return Reminder(
            createReminder = CreateReminder(repositories.reminderRepository, taskScheduler),
            updateReminder = UpdateReminder(repositories.reminderRepository, taskScheduler),
            deleteReminder = DeleteReminder(repositories.reminderRepository, taskScheduler)
        )
    }

    @Provides
    @Singleton
    fun provideTaskUseCases(
        repositories: AgendaRepositories,
        taskScheduler: TaskScheduler
    ): Task {
        return Task(
            createTask = CreateTask(repositories.taskRepository, taskScheduler),
            updateTask = UpdateTask(repositories.taskRepository, taskScheduler),
            deleteTask = DeleteTask(repositories.taskRepository, taskScheduler)
        )
    }

    @Provides
    @Singleton
    fun provideAgendaUseCases(
        repositories: AgendaRepositories,
        userPrefs: UserPreferences,
        event: Event,
        reminder: Reminder,
        task: Task
    ): AgendaUseCases {
        return AgendaUseCases(
            event = event,
            reminder = reminder,
            task = task,
            logout = Logout(repositories.agendaRepository, userPrefs)
        )
    }
}