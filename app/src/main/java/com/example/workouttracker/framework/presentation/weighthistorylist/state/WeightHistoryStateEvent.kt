package com.example.workouttracker.framework.presentation.weighthistorylist.state

import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.state.StateEvent

sealed class WeightHistoryStateEvent : StateEvent {

    class InsertWeightHistoryEvent(val weightHistory : WeightHistory) : WeightHistoryStateEvent()

    class DeleteWeightHistoryEvent(val weightHistory: WeightHistory) : WeightHistoryStateEvent()

    class DeleteWeightHistoryByIdEvent(val weightHistoryId : String) : WeightHistoryStateEvent()

    class UpdateWeightHistoryEvent(val weightHistory: WeightHistory) : WeightHistoryStateEvent()
}