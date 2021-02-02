package com.example.workouttracker.domain.model.weight_history

import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.util.DateUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightHistoryFactory
@Inject
constructor(
    private val dateUtil: DateUtil
)
{

    fun createSingleWeightHistory(
        id : String? = null,
        muscleEquipmentId: String,
        weight : Double,
        unit : String,
    ) : WeightHistory{
        return WeightHistory(
            id = id ?: UUID.randomUUID().toString(),
            muscleEquipmentId = muscleEquipmentId,
            weight = convertWeightToTwoDecimals(weight),
            unit = unit,
            createdAt = dateUtil.getCurrentTimestamp()
        )
    }

    private fun convertWeightToTwoDecimals(number : Double) : Double{
        return String.format("%.2f", number).toDouble()
    }
}