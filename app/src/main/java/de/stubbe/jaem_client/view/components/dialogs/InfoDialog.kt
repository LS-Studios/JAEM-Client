package de.stubbe.jaem_client.view.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.view.components.JAEMButton
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun InfoDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        dismissButton = {},
        confirmButton = {
            JAEMButton(
                text = stringResource(R.string.close),
                onClick = onDismissRequest
            )
        },
        shape = Dimensions.Shape.Rounded.Small,
        containerColor = JAEMThemeProvider.current.background,
        iconContentColor = JAEMThemeProvider.current.textPrimary,
        titleContentColor = JAEMThemeProvider.current.textPrimary,
        textContentColor = JAEMThemeProvider.current.textPrimary,
    )
}