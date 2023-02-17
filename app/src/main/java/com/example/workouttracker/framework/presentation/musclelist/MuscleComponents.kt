package com.example.workouttracker.framework.presentation.musclelist

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MuscleItemInputBackground(
    elevate: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
        shape = RectangleShape,
    ) {
        Row(
            modifier = modifier.animateContentSize(TweenSpec(300)),
            content = content
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MuscleInputText(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = text,
        onValueChange = onTextChanged,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        label = {
            Text(text = "What Muscle?")
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (text.isNotBlank()) {
                onImeAction()
            }
            keyboardController?.hide()
        }),
        modifier = modifier
    )
}

@Composable
fun MuscleEditButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    TextButton(
        onClick = onClick,
        shape = CircleShape,
        enabled = enabled,
        modifier = modifier
    ) {
        Text(text = text)
    }
}


@Composable
fun MuscleAlertDialog(
    openDialog : Boolean,
    dialogChange : (Boolean) -> Unit,
    title : String,
    message : String,
    confirmText : String,
    onConfirm : () -> Unit,
    cancelText : String
){
    if(openDialog){
        AlertDialog(
            onDismissRequest = { dialogChange(false) },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    dialogChange(false)
                }) {
                    Text(text = confirmText)
                }
            },
            dismissButton = {
                Button(onClick = {
                    dialogChange(false)
                }) {
                    Text(text = cancelText)
                }
            }
        )
    }

}