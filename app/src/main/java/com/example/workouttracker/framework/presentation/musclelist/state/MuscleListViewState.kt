package com.example.workouttracker.framework.presentation.musclelist.state

import android.os.Parcelable
import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.state.ViewState
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MuscleListViewState(
    var muscleList : ArrayList<Muscle>? = null,
    var newMuscle : Muscle? = null,
    var singleMuscleSearch : Muscle? = null,
    var deleteMuscle : Muscle? = null,
    var updatedMuscle : Muscle? = null

) : Parcelable, ViewState