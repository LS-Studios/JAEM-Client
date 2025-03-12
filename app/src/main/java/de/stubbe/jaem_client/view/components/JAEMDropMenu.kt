package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.JAEMMenuItemModel
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun JAEMDropMenu(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    menuItems: List<JAEMMenuItemModel>,
    offset: DpOffset = DpOffset.Zero
) {
    DropdownMenu(
        modifier = modifier,
        expanded = visible,
        onDismissRequest = onDismissRequest,
        shape = Dimensions.Shape.Rounded.Small,
        containerColor = JAEMThemeProvider.current.primary,
        offset = offset,
    ) {
        menuItems.forEachIndexed { index, menuItem ->
            DropdownMenuItem(
                text = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = menuItem.title,
                        style = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                            textAlign = if (menuItem.leadingIcon == null && menuItem.trailingIcon == null)
                                TextAlign.Center
                            else
                                TextAlign.Start
                        )
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
                    onDismissRequest()
                },
                contentPadding = PaddingValues(Dimensions.Padding.Medium)
            )

            if (index < menuItems.size - 1) {
                Divider()
            }
        }
    }
}