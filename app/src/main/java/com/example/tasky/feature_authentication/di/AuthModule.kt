package com.example.tasky.feature_authentication.di

import android.content.SharedPreferences
import com.example.tasky.common.Constants.API_KEY
import com.example.tasky.common.Constants.BASE_URL
import com.example.tasky.feature_authentication.data.remote.TaskyAuthApi
import com.example.tasky.feature_authentication.data.repository.AuthRepositoryImpl
import com.example.tasky.feature_authentication.data.util.ApiKeyInterceptor
import com.example.tasky.feature_authentication.data.util.UserPreferencesImpl
import com.example.tasky.feature_authentication.data.validation.EmailPatternValidatorImpl
import com.example.tasky.feature_authentication.domain.repository.AuthRepository
import com.example.tasky.feature_authentication.domain.use_case.AuthUseCases
import com.example.tasky.feature_authentication.domain.use_case.Authenticate
import com.example.tasky.feature_authentication.domain.use_case.Login
import com.example.tasky.feature_authentication.domain.use_case.Register
import com.example.tasky.feature_authentication.domain.util.UserPreferences
import com.example.tasky.feature_authentication.domain.validation.EmailPatternValidator
import com.example.tasky.feature_authentication.domain.validation.UserDataValidator
import com.example.tasky.feature_authentication.domain.validation.UserDataValidatorImpl
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
object AuthModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return  OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(API_KEY))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(client: OkHttpClient): TaskyAuthApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(prefs: SharedPreferences): UserPreferences {
        return UserPreferencesImpl(prefs)
    }

    @Provides
    @Singleton
    fun provideEmailPatternValidator(): EmailPatternValidator {
        return EmailPatternValidatorImpl()
    }

    @Provides
    @Singleton
    fun provideUserDataValidator(emailPatternValidator: EmailPatternValidator): UserDataValidator {
        return UserDataValidatorImpl(emailPatternValidator)
    }

   @Provides
   @Singleton
    fun provideAuthRepository(
        api: TaskyAuthApi,
        prefs: UserPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(api, prefs)
    }

    @Provides
    @Singleton
    fun provideUseCases(
        repository: AuthRepository,
        userDataValidator: UserDataValidator
    ): AuthUseCases {
        return AuthUseCases(
            register = Register(repository, userDataValidator),
            login = Login(repository, userDataValidator),
            authenticate = Authenticate((repository))
        )
    }
}