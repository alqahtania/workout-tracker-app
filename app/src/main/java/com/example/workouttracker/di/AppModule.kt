package com.example.workouttracker.di

import android.content.Context
import androidx.room.Room
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.datasource.cache.database.MuscleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideMuscleDb(
        @ApplicationContext context : Context
    ) : MuscleDatabase {
        return Room
            .databaseBuilder(context, MuscleDatabase::class.java, MuscleDatabase.DATABASE_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH)
        return sdf
    }


    @Singleton
    @Provides
    fun provideDateUtil(dateFormat: SimpleDateFormat): DateUtil {
        return DateUtil(
            dateFormat
        )
    }




}