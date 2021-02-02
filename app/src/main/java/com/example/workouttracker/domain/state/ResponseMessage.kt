package com.example.workouttracker.domain.state

data class ResponseMessage(
val message : String?,
val messageType: MessageType
)

sealed class MessageType{

    class Success: MessageType()

    class Error: MessageType()

}