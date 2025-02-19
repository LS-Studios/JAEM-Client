package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

/**
 * Ein einfacher horizontaler Trenner.
 * @param modifier Der Modifier, der auf den Trenner angewendet wird.
 */
@Composable
fun Divider(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.Border.ThinBorder)
            .background(JAEMThemeProvider.current.border.copy(
                alpha = 0.2f
            ))
    ) {}
}