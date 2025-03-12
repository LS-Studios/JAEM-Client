package de.stubbe.jaem_client.view.components.dialogs

import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextFieldColors
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.datastore.ServerUrlModel
import de.stubbe.jaem_client.model.DialogActionModel
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun AddServerUrlDialog(
    serverUrlModel: ServerUrlModel?,
    onDismissRequest: () -> Unit,
    onAddServerUrl: (ServerUrlModel) -> Unit
) {
    val context = LocalContext.current
    val invalidUrlString = stringResource(R.string.invalid_url)

    var name by remember { mutableStateOf(serverUrlModel?.name ?: "") }
    var url by remember { mutableStateOf(serverUrlModel?.url ?: "") }

    DialogBase(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.new_url),
        actions = listOf(
            DialogActionModel(
                text = stringResource(R.string.cancel),
                onClick = onDismissRequest
            ),
            DialogActionModel(
                text = stringResource(
                    if (serverUrlModel == null) R.string.add
                    else R.string.save
                ),
                onClick = {
                    val adjustedUrl = url.trimEnd('/').trim()

                    if (!URLUtil.isValidUrl(adjustedUrl)) {
                        Toast.makeText(
                            context,
                            invalidUrlString,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@DialogActionModel
                    }

                    onAddServerUrl(
                        ServerUrlModel.newBuilder().apply {
                            setName(name)
                            setUrl(adjustedUrl)
                        }.build()
                    )
                    onDismissRequest()
                }
            ),
        )
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = name,
            shape = Dimensions.Shape.Rounded.Small,
            onValueChange = { newValue ->
                name = newValue
            },
            textStyle = JAEMTextStyle(
                MaterialTheme.typography.titleMedium,
                color = JAEMThemeProvider.current.textPrimary
            ).copy(
                fontSize = Dimensions.FontSize.Medium,
                textAlign = TextAlign.Center
            ),
            label = {
                Text(
                    text = stringResource(R.string.server_name),
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleSmall,
                        color = JAEMThemeProvider.current.textSecondary
                    ),
                )
            },
            colors = JAEMTextFieldColors(),
            maxLines = 1,
            singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = url,
            shape = Dimensions.Shape.Rounded.Small,
            onValueChange = { newValue ->
                url = newValue
            },
            textStyle = JAEMTextStyle(
                MaterialTheme.typography.titleMedium,
                color = JAEMThemeProvider.current.textPrimary
            ).copy(
                fontSize = Dimensions.FontSize.Medium,
                textAlign = TextAlign.Center
            ),
            label = {
                Text(
                    text = stringResource(R.string.serevr_url),
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleSmall,
                        color = JAEMThemeProvider.current.textSecondary
                    ),
                )
            },
            colors = JAEMTextFieldColors(),
            maxLines = 1,
            singleLine = true,
        )
    }
}