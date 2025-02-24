package de.stubbe.jaem_client.view.variables

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

@Composable
fun JAEMTextStyle(
    baseStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    alpha: Float = 1.0f,
    fontFamily: FontFamily = Rationale,
    color: Color = JAEMThemeProvider.current.textPrimary
) = baseStyle.copy(
    color = color.copy(
        alpha = alpha
    ),
    fontFamily = fontFamily
)

@Composable
fun JAEMTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = JAEMThemeProvider.current.border,
    unfocusedIndicatorColor = JAEMThemeProvider.current.border,
    focusedTextColor = JAEMThemeProvider.current.textPrimary,
    unfocusedTextColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    cursorColor = JAEMThemeProvider.current.accent,
    focusedLabelColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    unfocusedLabelColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
)

const val durationFastMillis = 300
const val durationSlowMillis = 400

val jaemEnterHorizontally = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(durationMillis = durationFastMillis)
)

val jaemExitHorizontally = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(durationMillis = durationFastMillis)
)

val jaemPopEnterHorizontally = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(durationMillis = durationFastMillis)
)

val jaemPopExitHorizontally = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(durationMillis = durationFastMillis)
)

val jaemEnterVertically = slideInVertically(
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(durationMillis = durationFastMillis)
)

val jaemExitVertically = slideOutVertically(
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(durationMillis = durationSlowMillis)
)

val jeamFadeIn = fadeIn(animationSpec = tween(durationFastMillis))

val jeamFadeOut = fadeOut(animationSpec = tween(durationFastMillis))

val jeamAppearImmediately = fadeIn(animationSpec = tween(0))

val jeamDisappearImmediately = fadeOut(animationSpec = tween(0))