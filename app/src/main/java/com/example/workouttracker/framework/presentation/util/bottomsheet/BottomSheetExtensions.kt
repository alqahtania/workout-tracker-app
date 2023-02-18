package com.example.workouttracker.framework.presentation.util.bottomsheet

import android.R
import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.workouttracker.framework.presentation.ui.WorkoutTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Activity.showAsBottomSheet(
    wrapWithBottomSheetUI: Boolean = false,
    showCloseButton: Boolean = false,
    isDarkTheme: Boolean,
    backgroundColor: Color = Color.White,
    onSheetOpened: () -> Unit = {},
    onSheetClosed: () -> Unit = {},
    content: @Composable (() -> Unit) -> Unit
) {
    val viewGroup = this.findViewById(R.id.content) as ViewGroup
    addContentToView(
        showCloseButton,
        isDarkTheme,
        wrapWithBottomSheetUI,
        backgroundColor,
        onSheetOpened,
        onSheetClosed,
        viewGroup,
        content
    )
}

fun Fragment.showAsBottomSheet(
    wrapWithBottomSheetUI: Boolean = false,
    showCloseButton: Boolean = false,
    backgroundColor: Color = Color.White,
    isDarkTheme: Boolean,
    onSheetOpened: () -> Unit = {},
    onSheetClosed: () -> Unit = {},
    content: @Composable (() -> Unit) -> Unit
) {
    val viewGroup = requireActivity().findViewById(R.id.content) as ViewGroup
    addContentToView(
        showCloseButton,
        isDarkTheme,
        wrapWithBottomSheetUI,
        backgroundColor,
        onSheetOpened,
        onSheetClosed,
        viewGroup,
        content
    )
}

private fun addContentToView(
    showCloseButton: Boolean,
    isDarkTheme: Boolean,
    wrapWithBottomSheetUI: Boolean,
    backgroundColor: Color,
    onSheetOpened: () -> Unit,
    onSheetClosed: () -> Unit,
    viewGroup: ViewGroup,
    content: @Composable (() -> Unit) -> Unit
) {
    viewGroup.addView(
        ComposeView(viewGroup.context).apply {
            setContent {
                WorkoutTrackerTheme(darkTheme = isDarkTheme) {
                    BottomSheetWrapper(
                        showCloseButton,
                        wrapWithBottomSheetUI,
                        backgroundColor,
                        onSheetOpened,
                        onSheetClosed,
                        viewGroup,
                        this,
                        content
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomSheetWrapper(
    showCloseButton: Boolean,
    wrapWithBottomSheetUI: Boolean,
    backgroundColor: Color,
    onSheetOpened: () -> Unit,
    onSheetClosed: () -> Unit,
    parent: ViewGroup,
    composeView: ComposeView,
    content: @Composable (() -> Unit) -> Unit
) {
    val TAG = parent::class.java.simpleName
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState =
        rememberModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            confirmStateChange = {
                it != ModalBottomSheetValue.HalfExpanded
            }
        )
    var isSheetOpened by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetState = modalBottomSheetState,
        sheetContent = {
            when {
                wrapWithBottomSheetUI -> {
                    BottomSheetUIWrapper(
                        backgroundColor,
                        showCloseButton,
                        coroutineScope,
                        modalBottomSheetState
                    ) {
                        content {
                            animateHideBottomSheet(coroutineScope, modalBottomSheetState)
                        }
                    }
                }
                else -> content {
                    animateHideBottomSheet(coroutineScope, modalBottomSheetState)
                }
            }
        }
    ) {}


    BackHandler {
        animateHideBottomSheet(coroutineScope, modalBottomSheetState)
    }

    // Take action based on hidden state
    LaunchedEffect(modalBottomSheetState.currentValue) {
        when (modalBottomSheetState.currentValue) {
            ModalBottomSheetValue.Hidden -> {
                when {
                    isSheetOpened -> {
                        parent.removeView(composeView)
                        onSheetClosed()
                    }
                    else -> {
                        isSheetOpened = true
                        modalBottomSheetState.show()
                        onSheetOpened()
                    }
                }
            }
            else -> {
                Log.i(TAG, "Bottom sheet ${modalBottomSheetState.currentValue} state")
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun animateHideBottomSheet(
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState
) {
    coroutineScope.launch {
        modalBottomSheetState.hide() // will trigger the LaunchedEffect
    }
}