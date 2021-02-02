package com.example.workouttracker.domain.model.muscle

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Muscle(
    val id : String = UUID.randomUUID().toString(),
    val name : String,
    val createdAt : String
) : Parcelable