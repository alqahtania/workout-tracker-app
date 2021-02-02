package com.example.workouttracker.framework.presentation.weighthistorylist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.example.workouttracker.cache.abstraction.WeightHistoryDaoService
import com.example.workouttracker.cache.util.GenericErrors
import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.domain.state.MessageType
import com.example.workouttracker.domain.state.ResponseMessage
import com.example.workouttracker.domain.state.StateEvent
import com.example.workouttracker.framework.datasource.cache.mappers.WeightHistoryMapper
import com.example.workouttracker.framework.datasource.cache.model.WeightHistoryEntity
import com.example.workouttracker.framework.presentation.common.BaseViewModel
import com.example.workouttracker.framework.presentation.musclelist.state.MuscleListStateEvent
import com.example.workouttracker.framework.presentation.weighthistorylist.state.WeightHistoryStateEvent
import com.example.workouttracker.framework.presentation.weighthistorylist.state.WeightHistoryViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class WeightHistoryViewModel
@ViewModelInject
constructor(
    private val weightHistoryDaoService: WeightHistoryDaoService,
    private val mapper: WeightHistoryMapper
) : BaseViewModel<WeightHistoryViewState>() {

    private val _maxWeightHistory = MutableLiveData<WeightHistory>()

    val maxWeightHistory: LiveData<WeightHistory>
        get() = _maxWeightHistory

    val _muscleEquipId = MutableLiveData<String?>()

    val weightHistory = Transformations.switchMap(_muscleEquipId) { muscleEquipId ->
        muscleEquipId?.let {
            liveData {
                weightHistoryDaoService
                    .searchHistoryByMuscleEquipmentId(it)
                    .map {
                        setMaxValueWeight(it)
                        mapper.entityListToDomainList(it).reversed()
                    }
                    .catch { error ->
                        setResponseMessage(
                            responseMessage = ResponseMessage(
                                message = "${GenericErrors.ERROR_LOADING_WEIGHT_HISTORY}\nReason: ${error.message}",
                                messageType = MessageType.Error()
                            )
                        )
                    }
                    .collect {
                        emit(it)
                    }
            }
        }


    }




    override fun handleNewData(data: WeightHistoryViewState) {

        data.allWeightHistoryList?.let {
            setAllWeightHistory(it)
        }
        data.deletedWeightHistory?.let {
            setDeletedWeightHistory(it)
        }
        data.insertedWeightHistory?.let {
            setInsertedWeightHistory(it)
        }
        data.updatedWeightHistory?.let {
            setUpdatedWeightHistory(it)
        }
        data.weightHistoryByEquipList?.let {
            setWeightHistoryByEquipList(it)
        }
    }

    override fun handleNewResponseMessage(responseMessage: ResponseMessage) {
        setResponseMessage(responseMessage)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<WeightHistoryViewState>?> = when (stateEvent) {
            is WeightHistoryStateEvent.InsertWeightHistoryEvent -> {
                weightHistoryDaoService.insertWeightHistory(stateEvent.weightHistory)
            }
            is WeightHistoryStateEvent.DeleteWeightHistoryByIdEvent -> {
                weightHistoryDaoService.deleteAWeightHistoryById(stateEvent.weightHistoryId)
            }
            is WeightHistoryStateEvent.DeleteWeightHistoryEvent -> {
                weightHistoryDaoService.deleteAWeightHistory(stateEvent.weightHistory)
            }
            is WeightHistoryStateEvent.UpdateWeightHistoryEvent -> {
                weightHistoryDaoService.updateWeightHistory(stateEvent.weightHistory)
            }
            else -> {
                emitInvalidStateEvent(stateEvent)
            }

        }
        launchJob(job)
    }

    override fun initNewViewState(): WeightHistoryViewState {
        return WeightHistoryViewState()
    }

    fun setAllWeightHistory(weightHistoryList: ArrayList<WeightHistory>) {
        val update = getCurrentViewStateOrNew()
        update.allWeightHistoryList = weightHistoryList
        setViewState(update)
    }

    fun setDeletedWeightHistory(weightHistory: WeightHistory) {
        val update = getCurrentViewStateOrNew()
        update.deletedWeightHistory = weightHistory
        setViewState(update)
    }

    fun setInsertedWeightHistory(weightHistory: WeightHistory) {
        val update = getCurrentViewStateOrNew()
        update.insertedWeightHistory = weightHistory
        setViewState(update)
    }

    fun setUpdatedWeightHistory(weightHistory: WeightHistory) {
        val update = getCurrentViewStateOrNew()
        update.updatedWeightHistory = weightHistory
        setViewState(update)
    }

    fun setWeightHistoryByEquipList(weightHistoryList: ArrayList<WeightHistory>) {
        val update = getCurrentViewStateOrNew()
        update.weightHistoryByEquipList = weightHistoryList
        setViewState(update)
    }

    fun setMuscleEquipId(muscleEquipId: String) {
        _muscleEquipId.value = muscleEquipId
    }

    fun clearMuscleEquipId() {
        _muscleEquipId.value = null
    }

    suspend fun setMaxValueWeight(weightList: List<WeightHistoryEntity>) {
        val lbsList = weightList.filter {
            it.unit.contains("lbs")
        }
        val kgToLbsList = weightList.filter {
            it.unit.contains("kg")
        }.map {
            it.copy(weight = it.weight * MASS_VALUE)
        }

        val combine = lbsList + kgToLbsList
        val maxValueWeight = combine.maxByOrNull {
            it.weight
        }
        maxValueWeight?.let {
            var highestWeightInList: WeightHistoryEntity? = null
            for (item in weightList) {
                if (item.id == maxValueWeight.id) {
                    highestWeightInList = item
                }
            }
            highestWeightInList?.let {
                _maxWeightHistory.value = mapper.mapFromEntity(it)
            }
        }
    }

    //for updating a muscle
    private var currentEditPosition by mutableStateOf(-1)

    val currentEditItem: WeightHistory?
        get() = weightHistory.value?.getOrNull(currentEditPosition)

    fun onEditItemSelected(item: WeightHistory) {
        currentEditPosition = weightHistory.value?.indexOf(item) ?: -1
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

}