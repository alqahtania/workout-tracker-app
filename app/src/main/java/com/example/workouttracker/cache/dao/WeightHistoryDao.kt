package com.example.workouttracker.cache.dao

import androidx.room.*
import com.example.workouttracker.framework.datasource.cache.model.WeightHistoryEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface WeightHistoryDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeightHistory(weightHistoryEntity: WeightHistoryEntity) : Long

    @Query("SELECT * FROM weight_history")
    fun getAllWeightHistory() : Flow<List<WeightHistoryEntity>>

    @Query("SELECT * FROM weight_history WHERE muscle_equipment_id = :muscleEquipmentId")
    fun searchHistoryByMuscleEquipmentId(muscleEquipmentId : String) : Flow<List<WeightHistoryEntity>>

    @Delete
    suspend fun deleteAWeightHistory(weightHistory : WeightHistoryEntity) : Int

    @Query("DELETE FROM weight_history WHERE id = :weightHistoryId")
    suspend fun deleteAWeightHistoryById(weightHistoryId : String) : Int

    @Update
    suspend fun updateWeightHistory(weightHistoryEntity: WeightHistoryEntity) : Int
}