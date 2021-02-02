package com.example.workouttracker.framework.presentation.musclelist

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.workouttracker.cache.abstraction.MuscleDaoService
import com.example.workouttracker.cache.util.GenericErrors.ERROR_LOADING_DATA_LIST
import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.state.*
import com.example.workouttracker.framework.datasource.cache.mappers.MuscleMapper
import com.example.workouttracker.framework.datasource.preferences.PreferenceKeys.THEME_PREFERENCE
import com.example.workouttracker.framework.datasource.preferences.PreferencesValues.DARK_MODE
import com.example.workouttracker.framework.datasource.preferences.PreferencesValues.LIGHT_MODE
import com.example.workouttracker.framework.datasource.preferences.PreferencesValues.SYSTEM_MODE
import com.example.workouttracker.framework.presentation.common.BaseViewModel
import com.example.workouttracker.framework.presentation.musclelist.state.MuscleListStateEvent
import com.example.workouttracker.framework.presentation.musclelist.state.MuscleListViewState
import kotlinx.coroutines.flow.*

class MuscleViewModel
@ViewModelInject
constructor(
    private val muscleDaoService: MuscleDaoService,
    private val muscleMapper: MuscleMapper,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel<MuscleListViewState>() {

    private val _themeState = MutableLiveData<Int>()
    val themeState: LiveData<Int>
        get() = _themeState

    init {
        val themeState  = sharedPreferences.getInt(
            THEME_PREFERENCE,
            SYSTEM_MODE
        )
        Log.d("viewModelll", " themeState: $themeState ")
        setThemeState(themeState)
    }


    val muscleList = liveData {

        muscleDaoService.getAllMuscles()
            .map {
                muscleMapper.entityListToDomainList(it).reversed()
            }
            .catch { error ->
                setResponseMessage(
                    ResponseMessage(
                        message = "${ERROR_LOADING_DATA_LIST}\nReason: ${error.message}",
                        messageType = MessageType.Error()
                    )
                )
            }
            .collect {
                emit(it)
            }

    }

    override fun handleNewData(data: MuscleListViewState) {
        data.let {
            it.deleteMuscle?.let {
                setMuscleDeleted(it)
            }
            it.muscleList?.let {
                //this is never called. find out a way to collect the live flow somewhere else and return the data state
                setMuscleList(it)
            }
            it.newMuscle?.let {
                // for new muscle inserted
                setInsertedMuscle(it)
            }
            it.singleMuscleSearch?.let {
                setMuscleSearched(it)
            }
            it.updatedMuscle?.let {
                setUpdatedMuscle(it)
            }
        }
    }

    override fun handleNewResponseMessage(responseMessage: ResponseMessage) {
        setResponseMessage(responseMessage = responseMessage)
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<MuscleListViewState>?> = when (stateEvent) {
            is MuscleListStateEvent.InsertNewMuscleEvent -> {
                muscleDaoService.insertMuscle(stateEvent.muscle)
            }
            is MuscleListStateEvent.DeleteMuscleByIdEvent -> {
                muscleDaoService.deleteMuscleById(stateEvent.muscleId)
            }
            is MuscleListStateEvent.DeleteMuscleEvent -> {
                muscleDaoService.deleteMuscle(stateEvent.muscle)
            }
            is MuscleListStateEvent.SearchMuscleByIdEvent -> {
                muscleDaoService.searchMuscleById(stateEvent.muscleId)
            }
            is MuscleListStateEvent.UpdateMuscleEvent -> {
                muscleDaoService.updateMuscle(stateEvent.newMuscle)
            }
            else ->
                emitInvalidStateEvent(stateEvent)
        }

        launchJob(job)
    }


    fun setMuscleDeleted(muscle: Muscle) {
        val update = getCurrentViewStateOrNew()
        update.deleteMuscle = muscle
        setViewState(update)

    }

    fun setMuscleList(muscleList: ArrayList<Muscle>) {
        val update = getCurrentViewStateOrNew()
        update.muscleList = muscleList
        setViewState(update)
    }

    fun setInsertedMuscle(insertedMuscle: Muscle) {
        val update = getCurrentViewStateOrNew()
        update.newMuscle = insertedMuscle
        setViewState(update)
    }

    fun setMuscleSearched(muscle: Muscle) {
        val update = getCurrentViewStateOrNew()
        update.singleMuscleSearch = muscle
        setViewState(update)
    }

    fun setUpdatedMuscle(muscle: Muscle) {
        val update = getCurrentViewStateOrNew()
        update.updatedMuscle = muscle
        setViewState(update)
    }


    //for updating a muscle
    private var currentEditPosition by mutableStateOf(-1)

    val currentEditItem: Muscle?
        get() = muscleList.value?.getOrNull(currentEditPosition)

    fun onEditItemSelected(item: Muscle) {
        currentEditPosition = muscleList.value?.indexOf(item) ?: -1
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    fun onEditItemChange(item: Muscle) {
        val currentItem = requireNotNull(currentEditItem)
        require(currentItem.id == item.id) {
            "You can only change an item with the same id as currentEditItem"
        }

        val stateEvent = MuscleListStateEvent.UpdateMuscleEvent(item)
        setStateEvent(stateEvent)
    }

    fun saveThemeFilterOptions(themeState: Int) {
        editor.putInt(THEME_PREFERENCE, themeState)
        editor.apply()

        setThemeState(themeState = themeState)
    }

    private fun setThemeState(themeState : Int){
        _themeState.value = themeState
    }

    override fun initNewViewState(): MuscleListViewState {
        return MuscleListViewState()
    }
}