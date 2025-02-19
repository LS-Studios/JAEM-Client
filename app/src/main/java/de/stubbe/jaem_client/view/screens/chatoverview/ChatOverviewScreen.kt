package de.stubbe.jaem_client.view.screens.chatoverview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.model.ButtonActionModel
import de.stubbe.jaem_client.view.components.ButtonActions
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.viewmodel.AppViewModelProvider
import de.stubbe.jaem_client.viewmodel.ChatOverviewViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel

/**
 * Bildschirm für die Chat Übersicht
 *
 * @param navigationViewModel Navigation view model
 * @param viewModel Chat Übersicht view model
 */
@Composable
fun ChatOverviewScreen(
    navigationViewModel: NavigationViewModel,
    viewModel: ChatOverviewViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var searchValue by remember { mutableStateOf("") }

    val chats by viewModel.chats.collectAsState()

    val filteredChats = remember(searchValue, chats) { chats.filter {
        it.name.contains(searchValue, ignoreCase = true)
    } }

    ScreenBase(
        topBar = {
            ChatOverviewTopBar(searchValue) {
                searchValue = it
            }
        }
    ) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(top = Dimensions.Spacing.Small)
            ) {
                items(filteredChats) { chat ->
                    ChatRow(
                        navigationViewModel,
                        viewModel,
                        chat
                    )
                }
            }

            ButtonActions(
                modifier = Modifier
                    .padding(Dimensions.Spacing.Medium),
                actions = listOf(
                    ButtonActionModel(
                        icon = Icons.Default.Person,
                        contentDescription = stringResource(R.string.cd_profile_button),
                        alignment = Alignment.BottomStart,
                        subActions = listOf(
                            ButtonActionModel(
                                text = stringResource(R.string.change_profile),
                                icon = Icons.Default.SyncAlt,
                                contentDescription = stringResource(R.string.change_profile),
                            ) {

                            },
                            ButtonActionModel(
                                text = stringResource(R.string.share_profile),
                                icon = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share_profile),
                            ) {

                            },
                            ButtonActionModel(
                                text = stringResource(R.string.edit_profile),
                                icon = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_profile),
                            ) {

                            },
                            ButtonActionModel(
                                text = stringResource(R.string.add_profile),
                                icon = Icons.Default.AddCircleOutline,
                                contentDescription = stringResource(R.string.add_profile),
                            )
                        ),
                        onClick = {

                        }
                    ),
                    ButtonActionModel(
                        icon = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_chat),
                        alignment = Alignment.BottomEnd,
                        onClick = {

                        }
                    )
                )
            )
        }
    }
}