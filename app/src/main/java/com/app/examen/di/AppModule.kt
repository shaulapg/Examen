package com.app.examen.di

import android.content.Context
import com.app.examen.data.local.preferences.SudokuPreferences
import com.app.examen.data.remote.api.SudokuApi
import com.app.examen.data.repository.SudokuRepositoryImpl
import com.app.examen.domain.repository.SudokuRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SudokuModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://api.api-ninjas.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideSudokuApi(retrofit: Retrofit): SudokuApi =
        retrofit.create(SudokuApi::class.java)

    @Provides
    @Singleton
    fun provideSudokuPreferences(
        @ApplicationContext context: Context,
        gson: Gson,
    ): SudokuPreferences {
        return SudokuPreferences(context, gson)
    }

    @Provides
    @Singleton
    fun provideSudokuRepository(
        api: SudokuApi,
        preferences: SudokuPreferences,
    ): SudokuRepository{
    return SudokuRepositoryImpl(api, preferences)
    }
}