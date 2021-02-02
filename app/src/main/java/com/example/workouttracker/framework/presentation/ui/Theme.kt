package com.example.workouttracker.framework.presentation.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

private val DarkColorPalette = darkColors(
    primary = purple200,
    primaryVariant = purple700,
    secondary = teal200
)

private val LightColorPalette = lightColors(
    primary = blueGray600,
    onPrimary = white,
    primaryVariant = blueGray700,
    secondary = gray600,
    onSecondary = white,
//    background = blueGray300,
//    onBackground = black

    /* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun WorkoutTrackerTheme(
    darkTheme: Boolean,
    content: @Composable() () -> Unit
) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}