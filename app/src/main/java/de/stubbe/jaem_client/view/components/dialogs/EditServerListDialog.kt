package de.stubbe.jaem_client.view.components.dialogs

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dagger.hilt.android.EntryPointAccessors
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.data.di.ViewModelFactoryProvider
import de.stubbe.jaem_client.datastore.ServerUrlModel
import de.stubbe.jaem_client.model.DialogActionModel
import de.stubbe.jaem_client.model.JAEMMenuItemModel
import de.stubbe.jaem_client.model.enums.ServerListType
import de.stubbe.jaem_client.utils.simpleVerticalScrollbar
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.components.JAEMButton
import de.stubbe.jaem_client.view.components.JAEMDropMenu
import de.stubbe.jaem_client.view.components.NoItemsText
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.viewmodel.EditServerListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditServerListDialog(
    onDismissRequest: () -> Unit,
    serverListType: ServerListType,
    onUrlsChanged: (added: List<ServerUrlModel>, removed: List<ServerUrlModel>) -> Unit = { _, _ -> },
    joinOrLeaveServers: Boolean = true,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyScrollState = rememberLazyListState()

    val factory = remember { EntryPointAccessors.fromActivity(
        context as Activity,
        ViewModelFactoryProvider::class.java
    ) }

    val viewModel: EditServerListViewModel = remember{ factory.editServerListViewModelFactory().create(serverListType) }

    val urls by viewModel.urls.collectAsState()
    val editServerDialogIsOpen by viewModel.editServerDialogIsOpen.collectAsState()
    val serverUrlToEdit by viewModel.serverUrlToEdit.collectAsState()

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
                    coroutineScope.launch {
                        val addedAndRemoved = viewModel.saveUrls(joinOrLeaveServers)
                        onUrlsChanged(addedAndRemoved.first, addedAndRemoved.second)
                    }
                    onDismissRequest()
                }
            )
        ),
        actionDivider = true
    ) {
        Box(
            modifier = Modifier
                .height(IntrinsicSize.Min)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.Size.SuperHuge)
                    .simpleVerticalScrollbar(lazyScrollState),
                state = lazyScrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small),
                contentPadding = PaddingValues(bottom = Dimensions.Padding.Huge)
            ) {
                items(urls) { url ->
                    val scrollState = rememberScrollState()

                    var showDeleteMenu by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    showDeleteMenu = true
                                },
                                interactionSource = null,
                                indication = ripple(
                                    bounded = true,
                                )
                            )
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState),
                            text = url.name,
                            style = JAEMTextStyle().copy(
                                textAlign = TextAlign.Center
                            ),
                        )

                        JAEMDropMenu(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            visible = showDeleteMenu,
                            onDismissRequest = { showDeleteMenu = false },
                            menuItems = listOf(
                                JAEMMenuItemModel(
                                    title = stringResource(R.string.delete_url),
                                    leadingIcon = Icons.Default.Delete,
                                    onClick = {
                                        viewModel.removeUrl(url)
                                    }
                                ),
                                JAEMMenuItemModel(
                                    title = stringResource(R.string.edit_url),
                                    leadingIcon = Icons.Default.Edit,
                                    onClick = {
                                        viewModel.openEditServerDialog(url)
                                    }
                                )
                            ),
                            offset = DpOffset(50.dp, 0.dp)
                        )
                    }

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

            JAEMButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                icon = Icons.Default.Add,
                onClick = {
                    viewModel.openEditServerDialog(null)
                }
            )
        }
    }

    if (editServerDialogIsOpen) {
        AddServerUrlDialog(
            serverUrlModel = serverUrlToEdit,
            onDismissRequest = {
                viewModel.closeEditServerDialog()
            },
            onAddServerUrl = {
                if (serverUrlToEdit != null) {
                    viewModel.editUrl(serverUrlToEdit!!, it)
                } else {
                    viewModel.addUrl(it)
                }
            }
        )
    }
}