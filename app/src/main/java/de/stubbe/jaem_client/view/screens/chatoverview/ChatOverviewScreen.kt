package de.stubbe.jaem_client.view.screens.chatoverview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.database.entries.ChatRequestModel
import de.stubbe.jaem_client.model.ButtonActionModel
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.view.components.ButtonActions
import de.stubbe.jaem_client.view.components.ShareProfileBottomSheet
import de.stubbe.jaem_client.view.components.dialogs.GetSharedCodeDialog
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.ChatOverviewViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.ShareProfileViewModel

/**
 * Bildschirm für die Chat Übersicht
 *
 * @param navigationViewModel Navigation view model
 */
@Composable
fun ChatOverviewScreen(
    navigationViewModel: NavigationViewModel,
) {
    val viewModel: ChatOverviewViewModel = hiltViewModel()
    val shareProfileViewModel: ShareProfileViewModel = hiltViewModel()

    val userProfile by viewModel.userProfile.collectAsState()
    var searchValue by remember { mutableStateOf("") }

    var getSharedCodeDialogIsOpen by remember { mutableStateOf(false) }

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
                    .padding(Dimensions.Spacing.Large),
                actions = listOf(
                    ButtonActionModel(
                        icon = Icons.Default.Person,
                        contentDescription = stringResource(R.string.cd_profile_button),
                        alignment = Alignment.BottomStart,
                        subActions = listOf(
                            ButtonActionModel(
                                text = stringResource(R.string.share_profile),
                                icon = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share_profile),
                            ) {
                                if (userProfile != null) {
                                    shareProfileViewModel.openShareProfileBottomSheet(userProfile!!)
                                }
                            },
                            ButtonActionModel(
                                text = stringResource(R.string.edit_profile),
                                icon = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_profile),
                            ) {
                                if (userProfile != null) {
                                    navigationViewModel.changeScreen(
                                        NavRoute.EditProfile(
                                            userProfile!!.profile.uid,
                                            null
                                        )
                                    )
                                }
                            },
                            ButtonActionModel(
                                text = stringResource(R.string.search_for_profile),
                                icon = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_for_profile),
                            ) {

                            }
                        ),
                        onClick = {

                        }
                    ),
                    ButtonActionModel(
                        icon = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_chat),
                        alignment = Alignment.BottomEnd,
                        subActions = listOf(
                            ButtonActionModel(
                                text = stringResource(R.string.from_share_code),
                                icon = Icons.Default.Link,
                                contentDescription = stringResource(R.string.from_share_code),
                            ) {
                                getSharedCodeDialogIsOpen = true
                            },
                            ButtonActionModel(
                                text = stringResource(R.string.from_server),
                                icon = Icons.Default.Search,
                                contentDescription = stringResource(R.string.from_server),
                            ) {

                            },
                        ),
                        onClick = {

                        }
                    )
                )
            )
        }
    }

    if (getSharedCodeDialogIsOpen) {
        GetSharedCodeDialog(
            onDismissRequest = {
                getSharedCodeDialogIsOpen = false
            },
            onSubmit = { sharedCode ->
                navigationViewModel.changeScreen(NavRoute.EditProfile(null, sharedCode))
            }
        )
    }

    ShareProfileBottomSheet(
        onClose = {
            shareProfileViewModel.closeShareProfileBottomSheet()
        },
        shareProfileViewModel = shareProfileViewModel
    )
}

@Composable
fun RequestsRow(
    chatRequestModels: List<ChatRequestModel>,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = null,
                indication = ripple(
                    bounded = true
                )
            ) {
                onClick()
            }
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Tiny
            )
            .height(IntrinsicSize.Max)
        ,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimensions.Size.Medium)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                imageVector = Icons.Default.SaveAlt,
                contentDescription = stringResource(R.string.profile_description),
                tint = JAEMThemeProvider.current.textPrimary
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.requests),
            style = JAEMTextStyle(MaterialTheme.typography.titleLarge)
        )

        if (chatRequestModels.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .background(
                        JAEMThemeProvider.current.accent,
                        shape = CircleShape
                    )
                    .sizeIn(minWidth = Dimensions.Size.Tiny, maxHeight = Dimensions.Size.Tiny)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = chatRequestModels.size.toString(),
                    style = JAEMTextStyle(MaterialTheme.typography.titleSmall)
                )
            }
        }
    }
}