package com.example.workouttracker.framework.presentation.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workouttracker.cache.util.GenericErrors
import com.example.workouttracker.domain.state.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseViewModel<ViewState> : ViewModel() {


    private val _viewState = MutableLiveData<ViewState>()
    private val _responseMessage = MutableLiveData<ResponseMessage>()

    val viewState: LiveData<ViewState>
        get() = _viewState

    val responseMessage: LiveData<ResponseMessage>
        get() = _responseMessage

    val dataChannelManager = object : DataChannelManager<ViewState>() {
        override fun handleNewData(data: ViewState) {
            this@BaseViewModel.handleNewData(data = data)
        }

        override fun handleNewResponseMessage(responseMessage: ResponseMessage) {
            this@BaseViewModel.handleNewResponseMessage(responseMessage = responseMessage)
        }
    }

    abstract fun handleNewData(data: ViewState)
    abstract fun handleNewResponseMessage(responseMessage: ResponseMessage)
    abstract fun setStateEvent(stateEvent: StateEvent)


    fun emitInvalidStateEvent(stateEvent: StateEvent) = flow {
        emit(
            DataState.error<ViewState>(
                responseMessage = ResponseMessage(
                    messageType = MessageType.Error(),
                    message = GenericErrors.INVALID_STATE_EVENT
                )
            )
        )
    }

    fun setupChannel() = dataChannelManager.setupChannel()

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    fun setResponseMessage(responseMessage: ResponseMessage){
        _responseMessage.value = responseMessage
    }

    fun clearResponseMessage(){
        _responseMessage.value = null
    }
    fun launchJob(
        jobFunction: Flow<DataState<ViewState>?>
    ) = dataChannelManager.launchJob(jobFunction)

    fun getCurrentViewStateOrNew(): ViewState {
        return viewState.value ?: initNewViewState()
    }

    abstract fun initNewViewState(): ViewState

    fun cancelActiveJobs() = dataChannelManager.cancelJobs()
}