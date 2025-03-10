package de.stubbe.jaem_client.view.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.JameMenuItemModel
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun JAEMDropMenu(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    menuItems: List<JameMenuItemModel>,
) {
    DropdownMenu(
        modifier = modifier,
        expanded = visible,
        onDismissRequest = onDismissRequest,
        shape = Dimensions.Shape.Rounded.Small,
        containerColor = JAEMThemeProvider.current.primary,
    ) {
        menuItems.forEach { menuItem ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = menuItem.title,
                        style = JAEMTextStyle(MaterialTheme.typography.titleMedium)
                    )
                },
                leadingIcon = {
                    if (menuItem.leadingIcon != null) {
                        Icon(
                            imageVector = menuItem.leadingIcon,
                            contentDescription = null,
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                },
                trailingIcon = {
                    if (menuItem.trailingIcon != null) {
                        Icon(
                            imageVector = menuItem.trailingIcon,
                            contentDescription = null,
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                },
                onClick = {
                    menuItem.onClick()
                }
            )
        }
    }
}