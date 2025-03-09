package de.stubbe.jaem_client.data

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.view.variables.Rationale

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
    focusedTextColor = JAEMThemeProvider.current.textPrimary,
    unfocusedTextColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    disabledTextColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorTextColor = JAEMThemeProvider.current.error,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = JAEMThemeProvider.current.primary.copy(alpha = 0.5f),
    errorContainerColor = JAEMThemeProvider.current.error,
    cursorColor = JAEMThemeProvider.current.accent,
    errorCursorColor = JAEMThemeProvider.current.error,
    selectionColors = TextSelectionColors(
        handleColor = JAEMThemeProvider.current.accent,
        backgroundColor = JAEMThemeProvider.current.accent.copy(alpha = 0.4f)
    ),
    focusedIndicatorColor = JAEMThemeProvider.current.border,
    unfocusedIndicatorColor = JAEMThemeProvider.current.border,
    disabledIndicatorColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorIndicatorColor = JAEMThemeProvider.current.error,
    focusedLeadingIconColor = JAEMThemeProvider.current.textPrimary,
    unfocusedLeadingIconColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    disabledLeadingIconColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorLeadingIconColor = JAEMThemeProvider.current.error,
    focusedTrailingIconColor = JAEMThemeProvider.current.textPrimary,
    unfocusedTrailingIconColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    disabledTrailingIconColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorTrailingIconColor = JAEMThemeProvider.current.error,
    focusedLabelColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    unfocusedLabelColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    disabledLabelColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorLabelColor = JAEMThemeProvider.current.error,
    focusedPlaceholderColor = JAEMThemeProvider.current.textSecondary.copy(alpha = 0.8f),
    unfocusedPlaceholderColor = JAEMThemeProvider.current.textSecondary.copy(alpha = 0.6f),
    disabledPlaceholderColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorPlaceholderColor = JAEMThemeProvider.current.error,
    focusedSupportingTextColor = JAEMThemeProvider.current.textSecondary.copy(alpha = 0.8f),
    unfocusedSupportingTextColor = JAEMThemeProvider.current.textSecondary.copy(alpha = 0.6f),
    disabledSupportingTextColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorSupportingTextColor = JAEMThemeProvider.current.error,
    focusedPrefixColor = JAEMThemeProvider.current.textPrimary,
    unfocusedPrefixColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    disabledPrefixColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorPrefixColor = JAEMThemeProvider.current.error,
    focusedSuffixColor = JAEMThemeProvider.current.textPrimary,
    unfocusedSuffixColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.8f),
    disabledSuffixColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.6f),
    errorSuffixColor = JAEMThemeProvider.current.error
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