package com.example.workouttracker.framework.presentation.musclelist

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workouttracker.domain.model.muscle.Muscle
import com.example.workouttracker.domain.model.muscle.MuscleFactory
import com.example.workouttracker.domain.util.DateUtil


@Composable
fun MuscleScreen(
    items: List<Muscle>,
    onAddItem: (Muscle) -> Unit,
    currentlyEditing: Muscle?,
    onRemoveItem: (Muscle) -> Unit,
    onStartEdit: (Muscle) -> Unit,
    onItemClickedNavigate: (Muscle) -> Unit,
    onEditItemChanged: (Muscle) -> Unit,
    onEditDone: () -> Unit,
    dateUtil: DateUtil,
    muscleFactory: MuscleFactory
) {

    Column {
        val enableTopSection = currentlyEditing == null
        MuscleItemInputBackground(
            elevate = enableTopSection,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (enableTopSection) {
                MuscleItemEntryInput(onItemComplete = onAddItem, muscleFactory = muscleFactory)
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        muscle = item,
                        onItemLongClicked = {
                            onStartEdit(item)
                        },
                        onItemClicked = {
                            onItemClickedNavigate(item)
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .shadow(elevation = 8.dp, shape = CircleShape)
                            .background(color = Color.LightGray),
                        dateUtil = dateUtil
                    )
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }


    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MuscleRow(
    muscle: Muscle,
    onItemLongClicked: () -> Unit,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
    dateUtil: DateUtil
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onItemClicked()
                },
                onLongClick = { onItemLongClicked() }
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        Text(text = muscle.name)


//        Text(text = muscle.createdAt)
    }

}


@Composable
fun MuscleItemEntryInput(onItemComplete: (Muscle) -> Unit, muscleFactory: MuscleFactory) {
    val (text, setText) = remember { mutableStateOf("") }
    val submit = {
        onItemComplete(muscleFactory.createSingleMuscle(name = text))
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
    item: Muscle,
    onEditItemChanged: (Muscle) -> Unit,
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
            enabledSaveButton = enabledSaveButton,
            onRemoveItem = onRemoveItem,
            onEditDone = onEditDone
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

































