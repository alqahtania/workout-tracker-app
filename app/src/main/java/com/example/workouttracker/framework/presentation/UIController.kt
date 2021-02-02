package com.example.workouttracker.framework.presentation

import com.example.workouttracker.domain.state.ResponseMessage

interface UIController {

    fun onResponseReceived(
        responseMessage: ResponseMessage,
        responseMessageCallBack: ResponseMessageCallBack
        )
}

interface ResponseMessageCallBack{
    fun clearMessage()
}