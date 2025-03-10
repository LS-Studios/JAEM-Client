package de.stubbe.jaem_client.view.components.dialogs

import android.app.Activity
import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dagger.hilt.android.EntryPointAccessors
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextFieldColors
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.data.di.ViewModelFactoryProvider
import de.stubbe.jaem_client.model.DialogActionModel
import de.stubbe.jaem_client.model.JameMenuItemModel
import de.stubbe.jaem_client.model.enums.ServerListType
import de.stubbe.jaem_client.utils.simpleVerticalScrollbar
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.components.JAEMDropMenu
import de.stubbe.jaem_client.view.components.NoItemsText
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.EditServerListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditServerListDialog(
    onDismissRequest: () -> Unit,
    serverListType: ServerListType
) {
    val context = LocalContext.current
    val lazyScrollState = rememberLazyListState()

    val invalidUrlString = stringResource(R.string.invalid_url)

    val factory = EntryPointAccessors.fromActivity(
        context as Activity,
        ViewModelFactoryProvider::class.java
    )

    val viewModel: EditServerListViewModel = factory.editServerListViewModelFactory().create(serverListType)

    val urls by viewModel.urls.collectAsState()

    var urlInput by remember { mutableStateOf("") }

    DialogBase(
        onDismissRequest = onDismissRequest,
        title = stringResource(
            if (serverListType == ServerListType.MESSAGE_DELIVERY) R.string.edit_message_delivery_server_list
            else R.string.edit_uds_server_list
        ),
        actions = listOf(
            DialogActionModel(
                text = stringResource(R.string.close),
                onClick = onDismissRequest
            ),
            DialogActionModel(
                text = stringResource(R.string.save),
                onClick = {
                    viewModel.saveUrls()
                    onDismissRequest()
                }
            )
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(Dimensions.Size.Small, Dimensions.Size.SuperHuge)
                .simpleVerticalScrollbar(lazyScrollState),
            state = lazyScrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small)
        ) {
            items(urls) { url ->
                val scrollState = rememberScrollState()

                var showDeleteMenu by remember { mutableStateOf(false) }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                showDeleteMenu = true
                            },
                            interactionSource = null,
                            indication = ripple(
                                bounded = true,
                            )
                        ),
                    text = url,
                    style = JAEMTextStyle().copy(
                        textAlign = TextAlign.Center
                    ),
                )

                JAEMDropMenu(
                    visible = showDeleteMenu,
                    onDismissRequest = { showDeleteMenu = false },
                    menuItems = listOf(
                        JameMenuItemModel(
                            title = stringResource(R.string.delete_url),
                            onClick = {
                                viewModel.removeUrl(url)
                            }
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Dimensions.Spacing.Small))

                Divider(alpha = 0.1f)
            }
            item {
                if (urls.isEmpty()) {
                    NoItemsText(
                        text = stringResource(R.string.no_servers)
                    )
                }
            }
        }

        // Eingabefeld für neue URLs
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = urlInput,
                shape = Dimensions.Shape.Rounded.Small,
                onValueChange = { newUrl ->
                    urlInput = newUrl
                },
                textStyle = JAEMTextStyle(
                    MaterialTheme.typography.titleMedium,
                    color = JAEMThemeProvider.current.textPrimary
                ).copy(
                    fontSize = Dimensions.FontSize.Medium
                ),
                placeholder = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.new_url),
                        style = JAEMTextStyle(
                            MaterialTheme.typography.titleSmall,
                            color = JAEMThemeProvider.current.textSecondary
                        )
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (URLUtil.isValidUrl(urlInput)) {
                                viewModel.addUrl(urlInput)
                                urlInput = ""
                            } else {
                                Toast.makeText(context, invalidUrlString, Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_new_url_bt)
                        )
                    }
                },
                colors = JAEMTextFieldColors(),
                singleLine = true,
                maxLines = 1
            )
        }
    }
}