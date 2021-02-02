package com.example.workouttracker.di

import com.example.workouttracker.cache.abstraction.WeightHistoryDaoService
import com.example.workouttracker.cache.dao.WeightHistoryDao
import com.example.workouttracker.cache.implementation.WeightHistoryDaoServiceImpl
import com.example.workouttracker.domain.model.weight_history.WeightHistoryFactory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.datasource.cache.database.MuscleDatabase
import com.example.workouttracker.framework.datasource.cache.mappers.WeightHistoryMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object WeightHistoryModule {

    @Singleton
    @Provides
    fun provideWeightHistoryMapper(dateUtil: DateUtil): WeightHistoryMapper {
        return WeightHistoryMapper(dateUtil)
    }

    @Singleton
    @Provides
    fun provideWeightHistoryFactory(dateUtil: DateUtil): WeightHistoryFactory {
        return WeightHistoryFactory(dateUtil)
    }

    @Singleton
    @Provides
    fun provideWeightHistoryDao(muscleDatabase: MuscleDatabase): WeightHistoryDao {
        return muscleDatabase.weightHistoryDao()
    }

    @Singleton
    @Provides
    fun provideWeightHistoryDaoService(
        weightHistoryDao: WeightHistoryDao,
        weightHistoryMapper: WeightHistoryMapper
    ): WeightHistoryDaoService {
        return WeightHistoryDaoServiceImpl(weightHistoryDao, weightHistoryMapper)
    }
}