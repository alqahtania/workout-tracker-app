package com.example.workouttracker.framework.presentation.weighthistorylist

import androidx.compose.animation.transition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.model.weight_history.WeightHistory
import com.example.workouttracker.domain.model.weight_history.WeightHistoryFactory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.presentation.musclelist.ListItemsAnimationDefinitions
import com.example.workouttracker.framework.presentation.musclelist.ListItemsAnimationDefinitions.MAX_WIDTH
import com.example.workouttracker.framework.presentation.musclelist.MuscleAlertDialog
import com.example.workouttracker.framework.presentation.musclelist.PulseAnimationDefinitions
import org.w3c.dom.Text


@Composable
fun WeightHistoryScreen(
    items: List<WeightHistory>,
    onItemComplete: (weight: Double, unit: String) -> Unit,
    currentlyEditing: WeightHistory?,
    onStartEdit: (WeightHistory) -> Unit,
    onRemoveItem: (WeightHistory) -> Unit,
    onEditDone: () -> Unit,
    maxValueWeight: WeightHistory?,
    dateUtil: DateUtil
) {

    Column {
        //We moved the animation from inside the lazyColumn to avoid recomposition and reanimation
        val pulseAnim = transition(
            definition = ListItemsAnimationDefinitions.spreadDefinition,
            initState = ListItemsAnimationDefinitions.SpreadState.INITIAL,
            toState = ListItemsAnimationDefinitions.SpreadState.FINAL
        )
        val pulseMagnitude = pulseAnim[ListItemsAnimationDefinitions.spreadPropKey]
        val enableTopSection = currentlyEditing == null
        WeightHistoryItemInputBackground(
            elevate = enableTopSection,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (enableTopSection) {
                WeightItemEntryInput(onItemComplete = { weight, unit ->
                    onItemComplete(weight, unit)
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
                    val pulseAnim = transition(
                        definition = PulseAnimationDefinitions.pulseDefinition,
                        initState = PulseAnimationDefinitions.PulseState.INITIAL,
                        toState = PulseAnimationDefinitions.PulseState.FINAL
                    )
                    val pulseMag = pulseAnim[PulseAnimationDefinitions.pulsePropKey]
                    WeightHistoryEditRow(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillParentMaxWidth()
                            .shadow(elevation = 0.dp, shape = RectangleShape)
                            .border(
                                border = BorderStroke(
                                    1.dp,
                                    color = Color.Red.copy(alpha = pulseMag)
                                )
                            ),
                        weightHistory = item,
                        onRemoveItem = {
                            onRemoveItem(item)
                            onEditDone()
                        },
                        onEditDone = onEditDone,
                        dateUtil = dateUtil,
                        currentWidth = pulseMagnitude
                    )
                } else {
                    var modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillParentMaxWidth(pulseMagnitude)
                        .shadow(elevation = pulseMagnitude.dp, shape = CircleShape)
                    if(maxValueWeight?.id == item.id){
                        modifier = modifier.background(color = Color.Green)
                    }
                    WeightHistoryRow(
                        modifier = modifier,
                        weightHistory = item,
                        onItemLongClicked = {
                            onStartEdit(item)
                        },
                        dateUtil = dateUtil,
                        currentWidth = pulseMagnitude
                    )
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }
    }

}

@Composable
fun WeightHistoryRow(
    modifier: Modifier = Modifier,
    weightHistory: WeightHistory,
    onItemLongClicked: () -> Unit,
    onItemClicked: () -> Unit = {},
    currentWidth: Float,
    dateUtil: DateUtil,
    buttonSlot: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clickable(
                onClick = {
                    onItemClicked()
                },
                onLongClick = {onItemLongClicked()}
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
            if (MAX_WIDTH == currentWidth) {


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
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = dateUtil.removeTimeFromDateString(weightHistory.createdAt))
                }
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
    currentWidth: Float,
    onEditDone: () -> Unit,
    dateUtil: DateUtil,
) {

    WeightHistoryRow(
        modifier = modifier,
        weightHistory = weightHistory,
        onItemLongClicked = { },
        currentWidth = currentWidth,
        dateUtil = dateUtil
    ) {


        val (openDialog, dialogChange) = remember { mutableStateOf(false) }
        if (MAX_WIDTH == currentWidth) {

            Row(horizontalArrangement = Arrangement.Center) {
                val shrinkButtons = Modifier.widthIn(20.dp)

                TextButton(
                    onClick = { dialogChange(true) },
                    modifier = shrinkButtons
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = Color.Red,
                        modifier = Modifier
                            .width(30.dp)
                    )
                }

                TextButton(onClick = onEditDone, modifier = shrinkButtons) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = Color.Red,
                        modifier = Modifier.width(30.dp)
                    )
                }
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
    onItemComplete: (weight: Double, unit: String) -> Unit
) {
    val items = listOf("Unit", "lbs", "kg")
    val (itemIndex, onItemSelectedIndex) = remember { mutableStateOf(0) }
    val (numberValue, onNumberChanged) = remember { mutableStateOf("") }

    val submit = {
        onItemComplete(numberValue.toDouble(), items[itemIndex])
        onItemSelectedIndex(0)
        onNumberChanged("")
    }

    WeightItemInput(
        items = items,
        itemIndex = itemIndex,
        onItemSelectedIndex = onItemSelectedIndex,
        numberValue = numberValue,
        onNumberChanged = onNumberChanged,
        onItemComplete = submit
    ) {

        WeightAddButton(
            onClick = submit,
            text = "Add",
            enabled = numberValue.isNotBlank() && (itemIndex != 0)
        )
    }

}


@Composable
fun WeightItemInput(
    items: List<String>,
    itemIndex: Int,
    onItemSelectedIndex: (Int) -> Unit,
    numberValue: String,
    onNumberChanged: (String) -> Unit,
    onItemComplete: () -> Unit,
    buttonSlot: @Composable () -> Unit
) {

    Column {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
        {
            WeightInputField(
                items = items,
                itemIndex = itemIndex,
                onItemSelectedIndex = onItemSelectedIndex,
                numberValue = numberValue,
                onNumberChanged = onNumberChanged,
                onItemComplete = onItemComplete
            )

            Spacer(modifier = Modifier.width(8.dp))
            Box(Modifier.align(Alignment.CenterVertically)) { buttonSlot() }

        }
    }

}

const val MASS_VALUE = 2.2046226218