package com.example.workouttracker.framework.presentation.weighthistorylist.state

import android.os.Parcelable
import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeightHistoryViewState (
    var allWeightHistoryList : ArrayList<WeightHistory>? = null,
    var weightHistoryByEquipList : ArrayList<WeightHistory>? = null,
    var insertedWeightHistory : WeightHistory? = null,
    var deletedWeightHistory : WeightHistory? = null,
    var updatedWeightHistory : WeightHistory? = null
        ) : Parcelable, ViewState