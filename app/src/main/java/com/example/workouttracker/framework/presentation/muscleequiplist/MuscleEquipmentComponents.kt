package com.example.workouttracker.framework.presentation.muscleequiplist

import android.graphics.BitmapFactory
import androidx.compose.animation.animate
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.workouttracker.R
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.presentation.musclelist.ListItemsAnimationDefinitions
import java.util.*

@Composable
fun MuscleItemInputBackground(
    elevate: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val animatedElevation = animate(if (elevate) 1.dp else 0.dp, TweenSpec(500))
    Surface(
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
        elevation = animatedElevation,
        shape = RectangleShape,
    ) {
        Row(
            modifier = modifier.animateContentSize(animSpec = TweenSpec(300)),
            content = content
        )
    }
}


@Composable
fun MuscleInputText(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {}
) {

    TextField(
        value = text,
        onValueChange = onTextChanged,
        backgroundColor = Color.Transparent,
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        onImeActionPerformed = { action, softKeyboardController ->
            if (action == ImeAction.Done) {
                if (text.isNotBlank()) {
                    onImeAction()
                }
                softKeyboardController?.hideSoftwareKeyboard()
            }
        },
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
            .clickable(onClick = { onItemClicked() })
            .longPressGestureFilter { onItemLongClicked() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        if (ListItemsAnimationDefinitions.MAX_WIDTH == currentWidth) {
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
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clickable(onClick = { onImageClick() })
                                    .preferredHeightIn(min = 180.dp)
                                    .fillMaxWidth()
                            )
                        }
                    } else {
                        imageAvailable = false
                    }
                    if (!imageAvailable) {
                        Image(
                            bitmap = imageResource(id = R.drawable.placeholder_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clickable(onClick = { onImageClick() })
                                .preferredHeightIn(min = 180.dp)
                                .fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.preferredHeight(16.dp))

                    Text(
                        text = muscleEquip.name,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                }
            }
        }
    }


}


@Composable
fun FullImageDialog(
    openDialog: Boolean,
    dialogChange: (Boolean) -> Unit,
    muscleEquip: MuscleEquipment,
    imagePath: String,
) {
    if (openDialog) {
        Dialog(onDismissRequest = { dialogChange(false) }) {
//            Surface(
////                modifier = Modifier.fillMaxSize(),
//                shape = MaterialTheme.shapes.large
//            ) {

//                Box(
//
//                    contentAlignment = Alignment.Center
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .fillMaxWidth()
//                            .align(Alignment.TopCenter),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(text = muscleEquip.name)
//                        Icon(
//                            imageVector = Icons.Default.Close, modifier = Modifier.clickable(
//                                onClick = { dialogChange(false) }),
//                            tint = Color.Red
//                        )
//                    }

            val imageBitmap = BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
            imageBitmap?.let {
                Image(
                    bitmap = imageBitmap,
                    contentScale = ContentScale.Fit
                )
            }
        }


    }
}