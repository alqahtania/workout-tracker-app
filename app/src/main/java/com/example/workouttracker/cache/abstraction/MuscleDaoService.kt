package com.example.workouttracker.cache.abstraction

import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.domain.state.ViewState
import com.example.workouttracker.framework.datasource.cache.model.MuscleEntity
import com.example.workouttracker.framework.presentation.musclelist.state.MuscleListViewState
import kotlinx.coroutines.flow.Flow

interface MuscleDaoService {

    fun insertMuscle(muscle: Muscle): Flow<DataState<MuscleListViewState>?>


    fun searchMuscleById(id: String): Flow<DataState<MuscleListViewState>?>


    fun getAllMuscles(): Flow<List<MuscleEntity>>


    fun deleteMuscle(muscle: Muscle): Flow<DataState<MuscleListViewState>?>


    fun deleteMuscleById(muscleId: String): Flow<DataState<MuscleListViewState>?>


    fun updateMuscle(muscle: Muscle): Flow<DataState<MuscleListViewState>?>
}