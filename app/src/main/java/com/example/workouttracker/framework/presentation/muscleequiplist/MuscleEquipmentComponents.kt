package com.example.workouttracker.framework.presentation.muscleequiplist

import android.graphics.BitmapFactory
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.util.DateUtil
import java.util.*

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
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (text.isNotBlank()) {
                onImeAction()
            }
            keyboardController?.hide()
        }),
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
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
    openDialog: Boolean,
    dialogChange: (Boolean) -> Unit,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    cancelText: String
) {
    if (openDialog) {
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardRow(
    muscleEquip: MuscleEquipment,
    imagePath: String?,
    onImageClick: () -> Unit,
    onItemLongClicked: () -> Unit,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
    currentWidth: Float,
    dateUtil: DateUtil
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = {onItemClicked()},
                onLongClick = {onItemLongClicked()}
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
            Card(modifier) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* onClick */ }
                ) {
                    var imageAvailable: Boolean
                    if (!imagePath.isNullOrBlank()) {
                        val imageBitmap = BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
                        imageAvailable = imageBitmap != null
                        imageBitmap?.let {
                            Image(
                                bitmap = imageBitmap,
                                "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clickable(onClick = { onImageClick() })
                                    .heightIn(min = 180.dp)
                                    .fillMaxWidth()
                            )
                        }
                    } else {
                        imageAvailable = false
                    }
                    if (!imageAvailable) {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_image),
                            "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clickable(onClick = { onImageClick() })
                                .heightIn(min = 180.dp)
                                .fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = muscleEquip.name,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                }
            }
    }


}
