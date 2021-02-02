package com.example.workouttracker.cache.dao

import androidx.room.*
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.framework.datasource.cache.model.MuscleEquipmentEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MuscleEquipmentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMuscleEquipment(muscleEquipment : MuscleEquipmentEntity) : Long

    @Query("SELECT * FROM muscle_equipment")
    fun getAllMuscleEquipments() : Flow<List<MuscleEquipmentEntity>>

    @Query("SELECT * FROM muscle_equipment WHERE muscle_id = :muscleId")
    fun searchMuscleEquipByMuscleId(muscleId : String) : Flow<List<MuscleEquipmentEntity>>

    @Query("SELECT * FROM muscle_equipment WHERE id = :muscleEquipId")
    suspend fun searchMuscleEquipmentById(muscleEquipId : String) : MuscleEquipmentEntity?

    @Delete
    suspend fun deleteAMuscleEquipment(muscleEquipment: MuscleEquipmentEntity) : Int

    @Query("DELETE FROM muscle_equipment WHERE id = :muscleEquipmentId")
    suspend fun deleteAMuscleEquipmentById(muscleEquipmentId : String) : Int

    @Update
    suspend fun updateAMuscleEquipment(muscleEquipment: MuscleEquipmentEntity) : Int


}