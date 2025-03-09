package de.stubbe.jaem_client.view.components.dialogs

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.DEEP_LINK_URL
import de.stubbe.jaem_client.data.JAEMTextFieldColors
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun GetSharedCodeDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var sharedCode by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val copiedFromClipboardText = stringResource(R.string.shared_code_copied_from_clipboard)

    LaunchedEffect(Unit) {
        val clipboardContent = clipboardManager.getText()
        if (clipboardContent != null) {
            val urlPattern = "${DEEP_LINK_URL}/share/(\\w+)".toRegex()
            val matchResult = urlPattern.find(clipboardContent.text)
            if (matchResult != null) {
                Toast.makeText(context, copiedFromClipboardText, Toast.LENGTH_SHORT).show()
                sharedCode = matchResult.groupValues[1]
            }
        }
    }

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
                text = stringResource(R.string.enter_shared_code),
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
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = sharedCode,
                    shape = Dimensions.Shape.Rounded.Small,
                    onValueChange = { newValue ->
                        sharedCode = newValue
                    },
                    textStyle = JAEMTextStyle(
                        MaterialTheme.typography.titleMedium,
                        color = JAEMThemeProvider.current.textPrimary
                    ).copy(
                        fontSize = Dimensions.FontSize.Medium,
                        textAlign = TextAlign.Center
                    ),
                    placeholder = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.shared_code),
                            style = JAEMTextStyle(
                                MaterialTheme.typography.titleSmall,
                                color = JAEMThemeProvider.current.textSecondary
                            ).copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    },
                    colors = JAEMTextFieldColors(),
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
                ) {
                    DialogActionButton(
                        text = stringResource(R.string.cancel),
                        onClick = onDismissRequest
                    )
                    DialogActionButton(
                        text = stringResource(R.string.continue_),
                        onClick = {
                            onSubmit(sharedCode)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}