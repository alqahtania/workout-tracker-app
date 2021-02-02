package com.example.workouttracker.framework.presentation.musclelist

import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.workouttracker.framework.presentation.musclelist.ListItemsAnimationDefinitions.spreadDefinition
import com.example.workouttracker.framework.presentation.musclelist.ListItemsAnimationDefinitions.spreadPropKey

@Composable
fun PulsingDemo() {
    val color = MaterialTheme.colors.primary

    val pulseAnim = transition(
        definition = spreadDefinition,
        initState = ListItemsAnimationDefinitions.SpreadState.INITIAL,
        toState = ListItemsAnimationDefinitions.SpreadState.FINAL
    )

    val pulseMagnitude = pulseAnim[spreadPropKey]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(pulseMagnitude.dp)
                .width(pulseMagnitude.dp),
            imageVector = Icons.Default.Favorite.copy(),
        )
    }


    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
    ) {
        drawCircle(
            radius = pulseMagnitude,
            brush = SolidColor(color),
        )
    }
}


object ListItemsAnimationDefinitions {

    enum class SpreadState {
        INITIAL, FINAL
    }

    val spreadPropKey = FloatPropKey("pulseKey")
    val spreadDefinition = transitionDefinition<SpreadState> {
        state(SpreadState.INITIAL) { this[spreadPropKey] = 0.1f }
        state(SpreadState.FINAL) { this[spreadPropKey] = MAX_WIDTH }

        transition(
            SpreadState.INITIAL to SpreadState.FINAL,
        ) {
            spreadPropKey using repeatable(
                iterations = 1,
                animation = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        }
    }
    const val MAX_WIDTH = 2.0f
}


object PulseAnimationDefinitions{

    enum class PulseState{
        INITIAL, FINAL
    }

    val pulsePropKey = FloatPropKey("pulseKey")

    val pulseDefinition = transitionDefinition<PulseState> {
        state(PulseState.INITIAL) { this[pulsePropKey] = 0f}
        state(PulseState.FINAL) { this[pulsePropKey] = 1f}

        transition(
            PulseState.INITIAL to PulseState.FINAL,
        ) {
            pulsePropKey using infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        }
    }
}


object MovingTextAnimationDefinitions {

    enum class PulseState {
        INITIAL, MIDDLE, FINAL
    }

    val pulsePropKey = FloatPropKey("pulseKey")
    val pulseDefinition = transitionDefinition<PulseState> {
        state(PulseState.INITIAL) { this[pulsePropKey] = 0.0f }
        state(PulseState.MIDDLE) { this[pulsePropKey] = 1.0f }
        state(PulseState.FINAL) { this[pulsePropKey] = 2.0f }

        transition(
            PulseState.INITIAL to PulseState.FINAL
        ) {
            pulsePropKey using repeatable(
                iterations = 3,
                animation = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        }
    }
}


