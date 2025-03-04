package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

/**
 * Darstellung einer Dateiendung
 *
 * @param modifier Modifier
 * @param extension Dateiendung
 */
@Composable
fun ExtensionPresentation(
    modifier: Modifier = Modifier,
    extension: String
) {
    Text(
        modifier = modifier
            .padding(end = Dimensions.Padding.Small)
            .border(
                width = Dimensions.Border.ThinBorder,
                color = JAEMThemeProvider.current.border,
                shape = Dimensions.Shape.Rounded.Tiny
            )
            .padding(
                horizontal = Dimensions.Padding.Tiny
            ),
        text = extension,
        style = JAEMTextStyle(
            MaterialTheme.typography.labelSmall,
            color = JAEMThemeProvider.current.textSecondary
        )
    )
}