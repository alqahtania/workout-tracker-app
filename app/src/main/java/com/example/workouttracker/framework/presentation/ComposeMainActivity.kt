package com.example.workouttracker.framework.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.domain.state.MessageType
import com.example.workouttracker.domain.state.ResponseMessage
import com.example.workouttracker.framework.presentation.ui.WorkoutTrackerTheme

class ComposeMainActivity : AppCompatActivity(), UIController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            WorkoutTrackerTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    Greeting("Android")
//                }
//            }
        }
//    }

    override fun onResponseReceived(
        responseMessage: ResponseMessage,
        responseMessageCallBack: ResponseMessageCallBack
    ) {
        when (responseMessage.messageType) {
            is MessageType.Success -> {
                //TODO Do something with the success then clear the message

            }

            is MessageType.Error -> {
                //TODO Display snackbar with the error message then clear the message
            }
        }
    }
}



