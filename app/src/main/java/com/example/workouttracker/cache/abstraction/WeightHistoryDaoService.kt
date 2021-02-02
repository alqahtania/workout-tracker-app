package com.example.workouttracker.cache.abstraction

import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.framework.datasource.cache.model.WeightHistoryEntity
import com.example.workouttracker.framework.presentation.weighthistorylist.state.WeightHistoryViewState
import kotlinx.coroutines.flow.Flow

interface WeightHistoryDaoService {


    fun insertWeightHistory(weightHistory: WeightHistory) : Flow<DataState<WeightHistoryViewState>?>

    fun getAllWeightHistory() : Flow<List<WeightHistoryEntity>>

    fun searchHistoryByMuscleEquipmentId(muscleEquipmentId : String) : Flow<List<WeightHistoryEntity>>

    fun deleteAWeightHistory(weightHistory: WeightHistory) : Flow<DataState<WeightHistoryViewState>?>

    fun deleteAWeightHistoryById(weightHistoryId : String) : Flow<DataState<WeightHistoryViewState>?>

    fun updateWeightHistory(weightHistory: WeightHistory) : Flow<DataState<WeightHistoryViewState>?>
}