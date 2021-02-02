package com.example.workouttracker.cache.dao

import androidx.room.*
import com.example.workouttracker.framework.datasource.cache.model.MuscleEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MuscleDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMuscle(muscle : MuscleEntity) : Long

    @Query("SELECT * FROM muscle WHERE id = :id")
    suspend fun searchMuscleById(id : String) : MuscleEntity?

    @Query("SELECT * FROM muscle")
    fun getAllMuscles() : Flow<List<MuscleEntity>>

    @Delete
    suspend fun deleteMuscle(muscleEntity: MuscleEntity) : Int

    @Query("DELETE FROM muscle WHERE id = :muscleId")
    suspend fun deleteMuscleById(muscleId : String) : Int


    @Update
    suspend fun updateMuscle(muscle : MuscleEntity) : Int

}