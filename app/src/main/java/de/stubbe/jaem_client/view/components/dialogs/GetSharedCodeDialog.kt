package de.stubbe.jaem_client.view.components.dialogs

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
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
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.DEEP_LINK_URL
import de.stubbe.jaem_client.data.JAEMTextFieldColors
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.DialogActionModel
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

    DialogBase(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.enter_shared_code),
        actions = listOf(
            DialogActionModel(
                text = stringResource(R.string.cancel),
                onClick = onDismissRequest
            ),
            DialogActionModel(
                text = stringResource(R.string.continue_),
                onClick = {
                    onSubmit(sharedCode)
                    onDismissRequest()
                }
            )
        )
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
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
            maxLines = 1,
            singleLine = true
        )
    }
}