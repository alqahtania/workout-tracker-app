package com.example.workouttracker.framework.presentation.util.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

typealias OnClickedClose = () -> Unit

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    onClickedClose: OnClickedClose
) {
    Box(
        modifier.then(
            Modifier.padding(end = 13.dp, top = 13.dp)
        )
    ) {
        Button(
            onClick = { onClickedClose() },
            modifier = Modifier.size(26.dp),
            shape = CircleShape,
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
        }
    }
}