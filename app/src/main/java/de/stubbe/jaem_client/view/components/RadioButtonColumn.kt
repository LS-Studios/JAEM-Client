package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun <T, I> RadioButtonColumn(
    options: List<T>,
    getId: (T) -> I,
    getText: @Composable (T) -> String,
    selection: T,
    onOptionSelected: (T) -> Unit,
) {
    Column {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (getId(selection) == getId(option)),
                        onClick = { onOptionSelected(option) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = Dimensions.Padding.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (getId(selection) == getId(option)),
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = JAEMThemeProvider.current.textPrimary,
                        unselectedColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.5f),
                        disabledSelectedColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.4f),
                        disabledUnselectedColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.2f),
                    )
                )
                Text(
                    modifier = Modifier.padding(start = Dimensions.Padding.Medium),
                    text = getText(option),
                    style = JAEMTextStyle(MaterialTheme.typography.titleMedium),
                )
            }
        }
    }
}