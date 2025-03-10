package de.stubbe.jaem_client.view.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.DialogActionModel
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun DialogBase(
    onDismissRequest: () -> Unit,
    title: String,
    actions: List<DialogActionModel>,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.Large),
            colors = CardDefaults.cardColors(containerColor = JAEMThemeProvider.current.background),
            shape = Dimensions.Shape.Rounded.Small,
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.Small),
                text = title,
                style = JAEMTextStyle(
                    MaterialTheme.typography.titleLarge,
                    color = JAEMThemeProvider.current.textPrimary
                ).copy(
                    textAlign = TextAlign.Center
                )
            )

            Divider()

            Column(
                modifier = Modifier
                    .padding(Dimensions.Padding.Medium),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.Medium)
            ) {
                content()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
                ) {
                    actions.forEach { action ->
                        DialogActionButton(
                            text = action.text,
                            onClick = action.onClick
                        )
                    }
                }
            }
        }
    }
}