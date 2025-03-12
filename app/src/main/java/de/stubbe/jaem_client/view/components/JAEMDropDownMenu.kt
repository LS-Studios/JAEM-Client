package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.stubbe.jaem_client.data.JAEMTextFieldColors
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> JAEMDropDownMenu(
    items: List<T>,
    getTitle: @Composable (T) -> String,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            value = getTitle(selectedItem),
            shape = if (isExpanded) Dimensions.Shape.RoundedTop.Small else Dimensions.Shape.Rounded.Small,
            onValueChange = {},
            textStyle = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                textAlign = TextAlign.Center
            ),
            colors = JAEMTextFieldColors(),
            readOnly = true,
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            border = BorderStroke(Dimensions.Border.ThinBorder, JAEMTextFieldColors().focusedIndicatorColor),
            shape = if (isExpanded) Dimensions.Shape.RoundedBottom.Small else Dimensions.Shape.Rounded.Small,
            containerColor = JAEMThemeProvider.current.primary,
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = getTitle(item),
                            style = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
                if (index < items.size - 1) {
                    Divider()
                }
            }
        }
    }
}