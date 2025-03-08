package de.stubbe.jaem_client.view.components.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun RowScope.DialogActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = JAEMThemeProvider.current.background
        ),
        shape = Dimensions.Shape.Rounded.Small,
        border = BorderStroke(
            width = Dimensions.Border.ThinBorder,
            color = JAEMThemeProvider.current.border
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = Dimensions.Padding.Small,
                vertical = Dimensions.Padding.Small
            ),
            text = text
        )
    }
}