package com.example.workouttracker.domain.model.muscle_equipment

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.android.parcel.Parcelize
import java.io.File


data class MuscleEquipmentPhoto (
    val file : File,
    val imageBitmap : ImageBitmap
        )