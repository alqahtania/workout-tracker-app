package com.example.workouttracker.domain.model.weight_history

import android.os.Parcelable
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class WeightHistory(
    val id : String = UUID.randomUUID().toString(),
    val muscleEquipmentId: String,
    val weight : Double,
    val unit : String,
    val createdAt : String,
    val reps: Long? = null
) : Parcelable