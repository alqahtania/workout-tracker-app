package com.example.workouttracker.framework.presentation.weighthistorylist

import android.widget.Toast
import androidx.compose.animation.animate
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.workouttracker.domain.model.weight_history.WeightHistory


@Composable
fun WeightHistoryItemInputBackground(
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
fun DropdownDemo(
    items : List<String>,
    itemIndex : Int,
    onItemSelectedIndex : (Int) -> Unit

) {
    val disabledValue = items[0]
    var showMenu by remember { mutableStateOf( false ) }
    DropdownMenu(
        toggle = {
            Row(
                modifier = Modifier.width(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    items[itemIndex]
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown
                )

            }

        },
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
        toggleModifier = Modifier
            .clickable(onClick = { showMenu = true })
            .shadow(elevation = 2.dp, shape = CircleShape.copy(all = CornerSize(4.dp)))
            .padding(8.dp),
        dropdownModifier = Modifier
//                .background(Color.Red)
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(
                enabled = (item != disabledValue),
                onClick = {
                    onItemSelectedIndex(index)
                    showMenu = false
                }
            ) {

                Text(text = item)
            }
        }
    }
}


@Composable
fun WeightInputField(modifier : Modifier = Modifier,
                     items : List<String>,
                     itemIndex : Int,
                     onItemSelectedIndex : (Int) -> Unit,
                     numberValue : String,
                     onNumberChanged : (String) -> Unit,
                     onItemComplete : () -> Unit
){

    if(numberValue.contains(".")){
        val number = numberValue.substring(numberValue.indexOf("."))
        if(number.length > 3){
            val twoDecNumber = numberValue.substring(0, numberValue.indexOf(".") + 3)
            onNumberChanged(twoDecNumber)
        }
    }

    TextField(
        modifier = modifier,
        backgroundColor = Color.Transparent,
        value = numberValue,
        onValueChange = {
            onNumberChanged(it)
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        maxLines = 1,
        placeholder = {
            Text(text = "0")
        },
        trailingIcon = {
            DropdownDemo(
                items = items,
                itemIndex = itemIndex,
                onItemSelectedIndex = onItemSelectedIndex
            )

        },
        singleLine = true,
        onImeActionPerformed = { action, softKeybController ->
            if (action == ImeAction.Done) {
                if (numberValue.isNotBlank() && itemIndex != 0) {
                    onItemComplete()
                }
                softKeybController?.hideSoftwareKeyboard()
            }

        }
    )
}


@Composable
fun WeightAddButton(
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