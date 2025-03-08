package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun LoadingIfNull(
    vararg data: Any?,
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.Size.Small,
    strokeWidth: Dp = Dimensions.Border.MediumBorder,
    content: @Composable (data: Any) -> Unit
) {
    if (data.any { it == null }) {
        Box(
            modifier = modifier
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(size)
                    .align(Alignment.Center)
                    .padding(Dimensions.Padding.Small),
                strokeWidth = strokeWidth,
                color = JAEMThemeProvider.current.textPrimary,
                trackColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.2f)
            )
        }
    } else {
        content(data.filterNotNull())
    }
}