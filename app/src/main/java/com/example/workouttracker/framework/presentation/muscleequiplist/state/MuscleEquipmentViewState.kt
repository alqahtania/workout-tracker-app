package com.example.workouttracker.framework.presentation.muscleequiplist.state

import android.net.Uri
import android.os.Parcelable
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.state.ViewState
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class MuscleEquipmentViewState(
    var allMuscleEquipmentsList : ArrayList<MuscleEquipment>? = null,
    var muscleEquipByMuscleIdList : ArrayList<MuscleEquipment>? = null,
    var newInsertedNote : MuscleEquipment? = null,
    var singleMuscleEquipSearch : MuscleEquipment? = null,
    var deletedMuscleEquipment : MuscleEquipment? = null,
    var updatedMuscleEquipment : MuscleEquipment? = null,
    var newPhotoForMuscleEquip : MuscleEquipment? = null,
    var newPhotoDetail : PhotoDetail? = null
) : Parcelable, ViewState{

    @Parcelize
    data class PhotoDetail(
        val currentPhotoFile : File? = null,
        val oldPhotoFile: File? = null,
        val currentMuscleEquipId : String? = null,
        val imageUri : Uri? = null
    ) : Parcelable
}
