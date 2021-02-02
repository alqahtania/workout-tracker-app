package com.example.workouttracker.domain.model.muscle_equipment

import com.example.workouttracker.domain.util.DateUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleEquipmentFactory
@Inject
constructor(
    private val dateUtil: DateUtil
)
{

    fun createSingleMuscleEquipment(
        id : String? = null,
        muscleId : String,
        name : String,
    ) : MuscleEquipment{

        return MuscleEquipment(
            id = id ?: UUID.randomUUID().toString(),
            muscleId = muscleId,
            name = name,
            createdAt = dateUtil.getCurrentTimestamp()
        )
    }
}