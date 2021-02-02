package com.example.workouttracker.framework.presentation.muscleequiplist.state

import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.state.StateEvent

sealed class MuscleEquipmentStateEvent : StateEvent {

    class InsertNewMuscleEquipmentEvent(val muscleEquipment: MuscleEquipment) :
        MuscleEquipmentStateEvent()

    // this is returned as a flow from room can only be called from a coroutine context
//    class SearchMuscleEquipByMuscleIdEvent(val muscleId : String) : MuscleEquipmentStateEvent()

    class DeleteAMuscleEquipmentEvent(val muscleEquipment: MuscleEquipment) :
        MuscleEquipmentStateEvent()

    class DeleteAMuscleEquipmentById(val muscleEquipmentId: String) : MuscleEquipmentStateEvent()

    class UpdateAMuscleEquipmentEvent(val newMuscleEquipment: MuscleEquipment) :
        MuscleEquipmentStateEvent()

    class SearchSingleMuscleEquipEvent(val muscleEquipId: String) : MuscleEquipmentStateEvent()
}