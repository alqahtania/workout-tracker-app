package com.example.workouttracker.framework.presentation.musclelist.state

import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.state.StateEvent


sealed class MuscleListStateEvent : StateEvent{


    class SearchMuscleByIdEvent(val muscleId : String) : MuscleListStateEvent()

    class InsertNewMuscleEvent(val muscle : Muscle) : MuscleListStateEvent()

    class DeleteMuscleEvent(val muscle : Muscle) : MuscleListStateEvent()

    class DeleteMuscleByIdEvent(val muscleId : String) : MuscleListStateEvent()

    class UpdateMuscleEvent(val newMuscle : Muscle) : MuscleListStateEvent()
}