package com.example.workouttracker.framework.presentation.weighthistorylist

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.presentation.musclelist.MuscleAlertDialog


@Composable
fun WeightHistoryScreen(
    items: List<WeightHistory>,
    onItemComplete: (weight: Double, unit: String, reps: Long) -> Unit,
    currentlyEditing: WeightHistory?,
    onStartEdit: (WeightHistory) -> Unit,
    onRemoveItem: (WeightHistory) -> Unit,
    onEditDone: () -> Unit,
    maxValueWeight: WeightHistory?,
    dateUtil: DateUtil
) {

    Column {
        //We moved the animation from inside the lazyColumn to avoid recomposition and reanimation
        val enableTopSection = currentlyEditing == null
        WeightHistoryItemInputBackground(
            elevate = enableTopSection,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (enableTopSection) {
                WeightItemEntryInput(onItemComplete = { weight, unit, reps ->
                    onItemComplete(weight, unit, reps)
                })
            } else {
                Text(
                    "Editing item",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(14.dp)
                        .fillMaxWidth()
                )
            }

        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(items) { item ->
                if (currentlyEditing?.id == item.id) {
                    WeightHistoryEditRow(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillParentMaxWidth()
                            .shadow(elevation = 0.dp, shape = RectangleShape)
                            .border(
                                border = BorderStroke(
                                    1.dp,
                                    color = Color.Red
                                )
                            ),
                        weightHistory = item,
                        onRemoveItem = {
                            onRemoveItem(item)
                            onEditDone()
                        },
                        onEditDone = onEditDone,
                        dateUtil = dateUtil,
                    )
                } else {
                    var modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                    if (maxValueWeight?.id == item.id) {
                        modifier = modifier.background(color = Color.Green)
                    }
                    WeightHistoryRow(
                        modifier = modifier,
                        weightHistory = item,
                        onItemLongClicked = {
                            onStartEdit(item)
                        },
                        dateUtil = dateUtil,
                    )
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeightHistoryRow(
    modifier: Modifier = Modifier,
    weightHistory: WeightHistory,
    onItemLongClicked: () -> Unit,
    onItemClicked: () -> Unit = {},
    dateUtil: DateUtil,
    buttonSlot: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    onItemClicked()
                },
                onLongClick = { onItemLongClicked() }
            )
//            .longPressGestureFilter {  }
            .padding(16.dp)
    ) {

        var lbs = 0.0
        var kg = 0.0
        if (weightHistory.weight > 0) {
            if (weightHistory.unit.contains("lbs")) {
                lbs = weightHistory.weight
                kg = lbs / MASS_VALUE
            } else {
                kg = weightHistory.weight
                lbs = kg * MASS_VALUE
            }
        }
        Column {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "${convertWeightToTwoDecimals(lbs)} lbs")
                Text(text = "${convertWeightToTwoDecimals(kg)} kg")
                if (buttonSlot != null) {
                    Box(Modifier.align(Alignment.CenterVertically)) {
                        buttonSlot()
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "${weightHistory.reps} Reps")
                Text(text = dateUtil.removeTimeFromDateString(weightHistory.createdAt))
            }
        }
    }
}


private fun convertWeightToTwoDecimals(number: Double): Double {
    return String.format("%.2f", number).toDouble()
}

@Composable
fun WeightHistoryEditRow(
    modifier: Modifier = Modifier,
    weightHistory: WeightHistory,
    onRemoveItem: () -> Unit,
    onEditDone: () -> Unit,
    dateUtil: DateUtil,
) {

    WeightHistoryRow(
        modifier = modifier,
        weightHistory = weightHistory,
        onItemLongClicked = { },
        dateUtil = dateUtil
    ) {


        val (openDialog, dialogChange) = remember { mutableStateOf(false) }
        Row(horizontalArrangement = Arrangement.Center) {
            val shrinkButtons = Modifier.widthIn(20.dp)

            TextButton(
                onClick = { dialogChange(true) },
                modifier = shrinkButtons
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    "",
                    tint = Color.Red,
                    modifier = Modifier
                        .width(30.dp)
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
                message = "Are you sure you want to delete this item?",
                confirmText = "Delete",
                onConfirm = onRemoveItem,
                cancelText = "Cancel"
            )
        }
    }

}

@Composable
fun WeightItemEntryInput(
    onItemComplete: (weight: Double, unit: String, reps: Long) -> Unit
) {
    val items = listOf("Unit", "lbs", "kg")
    val (itemIndex, onItemSelectedIndex) = remember { mutableStateOf(0) }
    val (numberValue, onNumberChanged) = remember { mutableStateOf("") }
    val (repsValue, onRepsChanged) = remember { mutableStateOf("") }

    val submit = {
        onItemComplete(numberValue.toDouble(), items[itemIndex], repsValue.toLong())
        onItemSelectedIndex(0)
        onNumberChanged("")
        onRepsChanged("")
    }

    WeightItemInput(
        items = items,
        itemIndex = itemIndex,
        onItemSelectedIndex = onItemSelectedIndex,
        numberValue = numberValue,
        onNumberChanged = onNumberChanged,
        onItemComplete = submit,
        repsValue = repsValue,
        onRepsValueChanged = onRepsChanged
    ) {

        WeightAddButton(
            onClick = submit,
            text = "Add",
            enabled = numberValue.isNotBlank() && (itemIndex != 0) && repsValue.isNotBlank()
        )
    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WeightItemInput(
    items: List<String>,
    itemIndex: Int,
    onItemSelectedIndex: (Int) -> Unit,
    numberValue: String,
    repsValue: String,
    onRepsValueChanged: (String) -> Unit,
    onNumberChanged: (String) -> Unit,
    onItemComplete: () -> Unit,
    buttonSlot: @Composable () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            WeightInputField(
                modifier = Modifier.weight(0.5f),
                items = items,
                itemIndex = itemIndex,
                onItemSelectedIndex = onItemSelectedIndex,
                numberValue = numberValue,
                onNumberChanged = onNumberChanged,
                onItemComplete = onItemComplete,
                reps = repsValue
            )

            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                modifier = Modifier.weight(0.30f),
                value = repsValue, onValueChange = onRepsValueChanged,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (numberValue.isNotBlank() && itemIndex != 0 && repsValue.isNotBlank()) {
                        onItemComplete()
                    }
                    keyboardController?.hide()
                }),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                placeholder = {
                    Text(text = "Reps")
                },
                maxLines = 1
            )
            Box(
                Modifier
                    .align(Alignment.CenterVertically)
                    .weight(0.20f)) { buttonSlot() }

        }
    }

}

const val MASS_VALUE = 2.2046226218