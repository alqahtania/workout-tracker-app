package com.example.workouttracker.framework.datasource.cache.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.workouttracker.cache.dao.MuscleDao
import com.example.workouttracker.cache.dao.MuscleEquipmentDao
import com.example.workouttracker.cache.dao.WeightHistoryDao
import com.example.workouttracker.framework.datasource.cache.model.MuscleEntity
import com.example.workouttracker.framework.datasource.cache.model.MuscleEquipmentEntity
import com.example.workouttracker.framework.datasource.cache.model.WeightHistoryEntity


@Database(
    entities = [MuscleEntity::class,
        MuscleEquipmentEntity::class,
        WeightHistoryEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class MuscleDatabase : RoomDatabase() {

    abstract fun muscleDao() : MuscleDao
    abstract fun muscleEquipmentDao() : MuscleEquipmentDao
    abstract fun weightHistoryDao() : WeightHistoryDao

    companion object{
        const val DATABASE_NAME = "muscle_db"
    }
}