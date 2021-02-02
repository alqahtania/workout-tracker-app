package com.example.workouttracker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.workouttracker.cache.abstraction.MuscleDaoService
import com.example.workouttracker.cache.dao.MuscleDao
import com.example.workouttracker.cache.implementation.MuscleDaoServiceImpl
import com.example.workouttracker.domain.model.muscle.MuscleFactory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.datasource.cache.database.MuscleDatabase
import com.example.workouttracker.framework.datasource.cache.mappers.MuscleMapper
import com.example.workouttracker.framework.datasource.preferences.PreferenceKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object MuscleModule {

    @Singleton
    @Provides
    fun provideMuscleMapper(dateUtil: DateUtil) : MuscleMapper {
        return MuscleMapper(dateUtil = dateUtil)
    }


    @Singleton
    @Provides
    fun provideMuscleFactory(dateUtil: DateUtil) : MuscleFactory {
        return MuscleFactory(dateUtil)
    }

    @Singleton
    @Provides
    fun provideMuscleDao(muscleDatabase : MuscleDatabase) : MuscleDao {
        return muscleDatabase.muscleDao()
    }


    @Singleton
    @Provides
    fun provideMuscleDaoService(muscleDao: MuscleDao, muscleMapper: MuscleMapper) : MuscleDaoService {
        return MuscleDaoServiceImpl(muscleDao, muscleMapper)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context : Context
    ) : SharedPreferences {
        return context.getSharedPreferences(
            PreferenceKeys.MUSCLE_PREFERENCES,
            Context.MODE_PRIVATE
            )
    }

    @Singleton
    @Provides
    fun provideSharedPrefsEditor(
        sharedPreferences: SharedPreferences
    ): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

}