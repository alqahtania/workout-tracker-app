package com.example.workouttracker.cache.abstraction

import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.framework.datasource.cache.model.MuscleEquipmentEntity
import com.example.workouttracker.framework.presentation.muscleequiplist.state.MuscleEquipmentViewState
import kotlinx.coroutines.flow.Flow

interface MuscleEquipmentDaoService {


    fun insertMuscleEquipment(muscleEquipment: MuscleEquipment): Flow<DataState<MuscleEquipmentViewState>?>


    fun getAllMuscleEquipments(): Flow<List<MuscleEquipmentEntity>>


    fun searchMuscleEquipByMuscleId(muscleId: String): Flow<List<MuscleEquipmentEntity>>

    fun searchMuscleEquipmentById(muscleEquipId: String): Flow<DataState<MuscleEquipmentViewState>?>


    fun deleteAMuscleEquipment(muscleEquipment: MuscleEquipment): Flow<DataState<MuscleEquipmentViewState>?>


    fun deleteAMuscleEquipmentById(muscleEquipmentId: String): Flow<DataState<MuscleEquipmentViewState>?>


    fun updateAMuscleEquipment(updatedMuscleEquipment: MuscleEquipment): Flow<DataState<MuscleEquipmentViewState>?>
}