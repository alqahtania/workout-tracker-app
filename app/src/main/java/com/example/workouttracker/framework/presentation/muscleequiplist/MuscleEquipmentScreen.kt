package com.example.workouttracker.framework.presentation.muscleequiplist

import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipmentPhoto
import com.example.workouttracker.domain.util.DateUtil


@Composable
fun MuscleEquipmentScreen(
    items: List<MuscleEquipment>,
    photoPaths: Map<String, MuscleEquipmentPhoto>,
    onAddItem: (String) -> Unit,
    memoryCachedImages : LruCache<String, Bitmap>?,
    onImageIconClick: (MuscleEquipment) -> Unit,
    isImageLoading: Boolean,
    onImageClicked: (MuscleEquipmentPhoto) -> Unit,
    currentlyEditing: MuscleEquipment?,
    onRemoveItem: (MuscleEquipment) -> Unit,
    onStartEdit: (MuscleEquipment) -> Unit,
    onItemClickedNavigate: (MuscleEquipment) -> Unit,
    onEditItemChanged: (MuscleEquipment) -> Unit,
    onEditDone: () -> Unit,
    dateUtil: DateUtil
) {

    Column {
        val enableTopSection = currentlyEditing == null

        MuscleItemInputBackground(
            elevate = enableTopSection,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (enableTopSection) {
                MuscleItemEntryInput(onItemComplete = onAddItem)
            } else {
                Text(
                    "Editing item",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(12.dp)
                        .fillMaxWidth()
                )
            }

        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 8.dp)
        )
        {
            items(items) { item ->
                if (currentlyEditing?.id == item.id) {
                    MuscleItemInlineEditorEntryInput(
                        item = item,
                        onEditItemChanged = onEditItemChanged,
                        onEditDone = onEditDone,
                        onRemoveItem = {
                            onRemoveItem(item)
                            onEditDone()
                        }
                    )

                } else {
                    MuscleRow(
                        muscleEquip = item,
                        equipPhoto = photoPaths[item.id],
                        cachedBitmap = if(memoryCachedImages != null) memoryCachedImages[item.id] else null,
                        onItemLongClicked = {
                            onStartEdit(item)
                        },
                        onItemClicked = {
                            onItemClickedNavigate(item)
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        onImageIconClick = { onImageIconClick(item) },
                        onImageClicked = onImageClicked,
                        isImageLoading = isImageLoading,
                        dateUtil = dateUtil
                    )
                }
                Spacer(modifier = Modifier.padding(top = 16.dp))
            }
        }


    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MuscleRow(
    muscleEquip: MuscleEquipment,
    equipPhoto: MuscleEquipmentPhoto?,
    onImageClicked: (MuscleEquipmentPhoto) -> Unit,
    cachedBitmap : Bitmap?,
    onImageIconClick: () -> Unit,
    onItemLongClicked: () -> Unit,
    isImageLoading: Boolean,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
    dateUtil: DateUtil
) {
    Row(
        modifier = modifier.combinedClickable(onClick = { onItemClicked() }, onLongClick = {onItemLongClicked()}),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

            Card(
                modifier,
                elevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (equipPhoto != null) {

                        cachedBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clickable(onClick = { onImageClicked(equipPhoto) })
                                    .heightIn(max = 190.dp, min = 190.dp)
                                    .fillMaxWidth()
                            )
                        }



                    } else {
                        if (isImageLoading) {
                            Box(
                                modifier = Modifier
                                    .heightIn(max = 190.dp, min = 190.dp)
                                    .fillMaxWidth()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {

                            Image(
                                painter = painterResource(id = R.drawable.placeholder_image),
                                "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clickable(onClick = { /*TODO OPEN NEW PIC CAPTURE INTENT*/ })
                                    .heightIn(max = 190.dp, min = 190.dp)
                                    .fillMaxWidth()
                            )
                        }


                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = muscleEquip.name,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Text(
                                text = dateUtil.removeTimeFromDateString(muscleEquip.createdAt),
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Icon(
                            Icons.Default.AddAPhoto,
                            "",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable(onClick = { onImageIconClick() })
                                .width(30.dp)
                                .align(Alignment.CenterVertically)

                        )
                    }


                }
            }


        }

}


//@Composable
//fun MuscleRow(
//    muscleEquip: MuscleEquipment,
//    imagePath: String?,
//    onImageClick: () -> Unit,
//    onItemLongClicked: () -> Unit,
//    onItemClicked: () -> Unit,
//    modifier: Modifier = Modifier,
//    currentWidth: Float,
//    dateUtil: DateUtil
//) {
//    Row(
//        modifier = modifier
//            .clickable(onClick = { onItemClicked() })
//            .longPressGestureFilter { onItemLongClicked() }
//            .padding(16.dp),
//        horizontalArrangement = Arrangement.SpaceAround
//    ) {
//        if (ListItemsAnimationDefinitions.MAX_WIDTH == currentWidth) {
//            var imageAvailable : Boolean
//            if(!imagePath.isNullOrBlank()){
//                val imageBitmap = BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
//                imageAvailable = imageBitmap != null
//                imageBitmap?.let {
//                    Image(
//                        bitmap = imageBitmap,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .clickable(onClick = { onImageClick() })
//                            .size(40.dp)
//                            .clip(CircleShape)
//                    )
//                }
//            }else{
//                imageAvailable = false
//            }
//
//            if (!imageAvailable) {
//               Icon(
//                   Icons.Default.Add,
//                   modifier = Modifier.clickable(onClick = { onImageClick() })
//               )
//            }
//            Text(text = muscleEquip.name, modifier = Modifier.align(Alignment.CenterVertically))
//        }
//
//    }
//
//}
//
//


@Composable
fun MuscleItemEntryInput(onItemComplete: (String) -> Unit) {
    val (text, setText) = remember { mutableStateOf("") }
    val submit = {
        onItemComplete(text)
        setText("")
    }
    MuscleItemInput(
        text = text,
        onTextChanged = setText,
        submit = submit
    ) {
        MuscleEditButton(
            onClick = submit,
            text = "Add",
            enabled = text.isNotBlank()
        )
    }


}

@Composable
fun MuscleItemInput(
    text: String,
    onTextChanged: (String) -> Unit,
    submit: () -> Unit,
    buttonSlot: @Composable () -> Unit
) {

    Column {
        Row(
            Modifier
                .padding(horizontal = 8.dp)

        ) {
            MuscleInputText(
                text = text,
                onTextChanged = onTextChanged,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                onImeAction = submit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(Modifier.align(Alignment.CenterVertically)) { buttonSlot() }
        }
    }
}


@Composable
fun MuscleItemInlineEditorEntryInput(
    item: MuscleEquipment,
    onEditItemChanged: (MuscleEquipment) -> Unit,
    onEditDone: () -> Unit,
    onRemoveItem: () -> Unit
) {
    val (text, setText) = remember { mutableStateOf(item.name) }

    val submit = {
        onEditItemChanged(item.copy(name = text))
        onEditDone()
    }

    MuscleItemInlineEditor(
        text = text,
        onTextChanged = setText,
        submit = submit,
        onRemoveItem = onRemoveItem,
        onEditDone = onEditDone,
        enabledSaveButton = text.isNotBlank()
    )
}


@Composable
fun MuscleItemInlineEditor(
    text: String,
    onTextChanged: (String) -> Unit,
    submit: () -> Unit,
    enabledSaveButton: Boolean,
    onRemoveItem: () -> Unit,
    onEditDone: () -> Unit
) {

    MuscleItemInput(
        text = text,
        onTextChanged = onTextChanged,
        submit = submit
    ) {

        MuscleUpdatingButtons(
            submit = submit,
            onRemoveItem = onRemoveItem,
            onEditDone = onEditDone,
            enabledSaveButton = enabledSaveButton
        )


    }

}


@Composable
fun MuscleUpdatingButtons(
    submit: () -> Unit,
    enabledSaveButton: Boolean,
    onRemoveItem: () -> Unit,
    onEditDone: () -> Unit
) {
    val (openDialog, dialogChange) = remember { mutableStateOf(false) }
    Row {
        val shrinkButtons = Modifier.widthIn(20.dp)
        TextButton(onClick = submit, modifier = shrinkButtons, enabled = enabledSaveButton) {
            Icon(
                imageVector = Icons.Default.Save,
                "",
                tint = if (enabledSaveButton) Color.Black else Color.LightGray,
                modifier = Modifier.width(30.dp)
            )
        }

        TextButton(onClick = { dialogChange(true) }, modifier = shrinkButtons) {
            Icon(
                imageVector = Icons.Default.Delete,
                "",
                tint = Color.Red,
                modifier = Modifier.width(30.dp)
            )
        }

        TextButton(onClick = onEditDone, modifier = shrinkButtons) {
            Icon(
                imageVector = Icons.Default.Close,
                "",
                tint = Color.Red,
                modifier = Modifier.width(30.dp)
            )
        }
    }
    if (openDialog) {
        MuscleAlertDialog(
            openDialog = openDialog,
            dialogChange = dialogChange,
            title = "Delete Item",
            message = "Deleting this item will delete all records associated with it! Do you want to proceed?",
            confirmText = "Delete",
            onConfirm = onRemoveItem,
            cancelText = "Cancel"
        )
    }


}