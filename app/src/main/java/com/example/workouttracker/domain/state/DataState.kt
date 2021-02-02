package com.example.workouttracker.domain.state


data class DataState<T>(
    val responseMessage: ResponseMessage? = null,
    val data: T? = null
) {

    companion object {

        fun <T> error(
            responseMessage: ResponseMessage
        ): DataState<T> {
            return DataState(
                responseMessage = responseMessage,
                data = null
            )
        }

        fun <T> data(
            responseMessage: ResponseMessage? = null,
            data: T? = null
        ) : DataState<T>{
            return DataState(
                responseMessage = responseMessage,
                data = data
            )
        }
    }
}