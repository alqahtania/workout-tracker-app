package com.example.workouttracker.domain.model.muscle_equipment

import android.os.Parcelable
import com.example.workouttracker.domain.model.muscle.Muscle
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class MuscleEquipment(
    val id : String = UUID.randomUUID().toString(),
    val muscleId: String,
    val name : String,
    val createdAt : String
) : Parcelable