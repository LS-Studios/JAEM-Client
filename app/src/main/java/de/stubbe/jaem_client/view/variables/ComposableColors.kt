package de.stubbe.jaem_client.view.variables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

@Composable
fun JaemTextStyle(
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