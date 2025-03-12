package de.stubbe.jaem_client.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun JAEMCheckBox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    center: Boolean = true
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = checked,
                onClick = { onCheckedChange(!checked) },
                role = Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (center) Arrangement.Center else Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkmarkColor = JAEMThemeProvider.current.background,
                checkedColor = JAEMThemeProvider.current.textPrimary,
                uncheckedColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.5f),
                disabledCheckedColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.4f),
                disabledUncheckedColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.2f),
                disabledIndeterminateColor = JAEMThemeProvider.current.textPrimary.copy(alpha = 0.2f),
            )
        )
        Text(
            text = text,
            style = JAEMTextStyle(MaterialTheme.typography.titleMedium),
        )
    }
}