package com.example.workouttracker.cache

import com.example.workouttracker.cache.CacheErrors.CACHE_DATA_NULL
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.domain.state.MessageType
import com.example.workouttracker.domain.state.ResponseMessage

abstract class CacheResponseHandler<ViewState, Data>(
    private val response: CacheResult<Data?>
) {
    suspend fun getResult(): DataState<ViewState>? {
        return when (response) {
            is CacheResult.GenericError -> {
                DataState.error(
                    responseMessage = ResponseMessage(
                        message = response.errorMessage,
                        messageType = MessageType.Error()
                    )
                )
            }
            is CacheResult.Success -> {
                if (response.value == null) {
                    DataState.error(
                        responseMessage = ResponseMessage(
                            message = CACHE_DATA_NULL,
                            messageType = MessageType.Error()
                        )
                    )
                } else {
                    handleSuccess(resultObject = response.value)
                }
            }
        }
    }

    abstract fun handleSuccess(resultObject: Data): DataState<ViewState>?
}