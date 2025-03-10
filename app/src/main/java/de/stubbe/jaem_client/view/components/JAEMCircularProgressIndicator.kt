package de.stubbe.jaem_client.view.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun JAEMCircularProgressIndicator(
    strokeWidth: Dp = Dimensions.Border.MediumBorder,
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier,
        strokeWidth = strokeWidth,
        color = JAEMThemeProvider.current.textPrimary,
        trackColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.2f)
    )
}