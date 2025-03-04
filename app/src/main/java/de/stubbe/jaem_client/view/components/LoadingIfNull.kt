package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun LoadingIfNull(
    vararg data: Any?,
    modifier: Modifier = Modifier,
    content: @Composable (data: Any) -> Unit
) {
    if (data.any { it == null }) {
        CircularProgressIndicator(
            modifier = modifier
                .padding(Dimensions.Padding.Small),
            color = JAEMThemeProvider.current.textPrimary,
            trackColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.2f)
        )
    } else {
        content(data.filterNotNull())
    }
}