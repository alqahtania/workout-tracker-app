package com.example.workouttracker.di

import com.example.workouttracker.cache.abstraction.MuscleEquipmentDaoService
import com.example.workouttracker.cache.dao.MuscleEquipmentDao
import com.example.workouttracker.cache.implementation.MuscleEquipeDaoServiceImpl
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipmentFactory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.datasource.cache.database.MuscleDatabase
import com.example.workouttracker.framework.datasource.cache.mappers.MuscleEquipmentMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MuscleEquipModule {


    @Singleton
    @Provides
    fun provideMuscleEquipMapper(dateUtil: DateUtil): MuscleEquipmentMapper {
        return MuscleEquipmentMapper(dateUtil)
    }

    @Singleton
    @Provides
    fun provideMuscleEquipFactory(dateUtil: DateUtil): MuscleEquipmentFactory {
        return MuscleEquipmentFactory(dateUtil)
    }

    @Singleton
    @Provides
    fun provideMuscleEquipmentDao(muscleDatabase: MuscleDatabase): MuscleEquipmentDao {
        return muscleDatabase.muscleEquipmentDao()
    }

    @Singleton
    @Provides
    fun provideMuscleEquipmentDaoService(
        muscleEquipmentDao: MuscleEquipmentDao,
        muscleEquipmentMapper: MuscleEquipmentMapper
    ): MuscleEquipmentDaoService {
        return MuscleEquipeDaoServiceImpl(muscleEquipmentDao, muscleEquipmentMapper)
    }
}