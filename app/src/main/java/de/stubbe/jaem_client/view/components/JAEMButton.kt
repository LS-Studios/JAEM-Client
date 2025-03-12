package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun JAEMButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = JAEMThemeProvider.current.background
        ),
        shape = Dimensions.Shape.Rounded.Small,
        border = BorderStroke(
            width = Dimensions.Border.ThinBorder,
            color = JAEMThemeProvider.current.border
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Tiny),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (text != null && icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = JAEMThemeProvider.current.textPrimary
                )
                Text(
                    text = text,
                    style = JAEMTextStyle(MaterialTheme.typography.bodyMedium).copy(
                        textAlign = TextAlign.Center
                    )
                )
            } else if (text != null) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = Dimensions.Padding.Small,
                        vertical = Dimensions.Padding.Small
                    ),
                    text = text,
                    style = JAEMTextStyle(MaterialTheme.typography.bodyMedium).copy(
                        textAlign = TextAlign.Center
                    )
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
        }
    }
}