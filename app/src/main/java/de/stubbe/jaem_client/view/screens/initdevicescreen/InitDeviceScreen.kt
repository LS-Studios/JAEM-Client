package de.stubbe.jaem_client.view.screens.initdevicescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.hilt.navigation.compose.hiltViewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextFieldColors
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import de.stubbe.jaem_client.model.JAEMMenuItemModel
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.enums.ServerListType
import de.stubbe.jaem_client.view.MainActivity
import de.stubbe.jaem_client.view.components.JAEMButton
import de.stubbe.jaem_client.view.components.JAEMCheckBox
import de.stubbe.jaem_client.view.components.JAEMDropMenu
import de.stubbe.jaem_client.view.components.LoadingOverlay
import de.stubbe.jaem_client.view.components.ProfilePicture
import de.stubbe.jaem_client.view.components.dialogs.EditServerListDialog
import de.stubbe.jaem_client.view.components.filepicker.JAEMPickFileAndCrop
import de.stubbe.jaem_client.view.components.topbars.TitleWithActionsTopBar
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.InitDeviceViewModel
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun InitDeviceScreen(
    navigationViewModel: NavigationViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel: InitDeviceViewModel = hiltViewModel()

    val profilePicture by viewModel.profilePicture.collectAsState()
    val profileName by viewModel.name.collectAsState()
    val profileDescription by viewModel.description.collectAsState()
    val allowProfileSharing by viewModel.allowProfileSharing.collectAsState()

    val isMessageDeliveryDialogOpen by viewModel.isMessageDeliveryDialogOpen.collectAsState()
    val isUdsDialogOpen by viewModel.isUdsDialogOpen.collectAsState()
    val imagePickerIsOpen by viewModel.imagePickerIsOpen.collectAsState()

    val initializingProfile by viewModel.initializingProfile.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    BackHandler {
        (context as? MainActivity)?.finish()
    }

    ScreenBase(
        topBar = {
            TitleWithActionsTopBar(
                title = stringResource(R.string.init_device),
                leadingActions = {
                    IconButton(
                        onClick = {
                            showThemeDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = stringResource(R.string.theme),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                    JAEMDropMenu(
                        visible = showThemeDialog,
                        onDismissRequest = {
                            showThemeDialog = false
                        },
                        menuItems = listOf(
                            JAEMMenuItemModel(
                                title = stringResource(R.string.system),
                                onClick = {
                                    viewModel.changeTheme(Theme.SYSTEM)
                                }
                            ),
                            JAEMMenuItemModel(
                                title = stringResource(R.string.light),
                                onClick = {
                                    viewModel.changeTheme(Theme.LIGHT)
                                }
                            ),
                            JAEMMenuItemModel(
                                title = stringResource(R.string.dark),
                                onClick = {
                                    viewModel.changeTheme(Theme.DARK)
                                }
                            ),
                            JAEMMenuItemModel(
                                title = stringResource(R.string.crypto),
                                onClick = {
                                    viewModel.changeTheme(Theme.CRYPTO)
                                }
                            ),
                        ),
                    )
                },
                trailingActions = {
                    IconButton(
                        onClick = {
                            showLanguageDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(R.string.close_bt),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                    JAEMDropMenu(
                        visible = showLanguageDialog,
                        onDismissRequest = {
                            showLanguageDialog = false
                        },
                        menuItems = listOf(
                            JAEMMenuItemModel(
                                title = stringResource(R.string.german),
                                onClick = {
                                    viewModel.changeLanguage(
                                        context = context,
                                        language = UserPreferences.Language.GERMAN
                                    )
                                }
                            ),
                            JAEMMenuItemModel(
                                title = stringResource(R.string.english),
                                onClick = {
                                    viewModel.changeLanguage(
                                        context = context,
                                        language = UserPreferences.Language.ENGLISH
                                    )
                                }
                            ),
                            JAEMMenuItemModel(
                                title = stringResource(R.string.russian),
                                onClick = {
                                    viewModel.changeLanguage(
                                        context = context,
                                        language = UserPreferences.Language.Russian
                                    )
                                }
                            ),
                            JAEMMenuItemModel(
                                title = stringResource(R.string.korean),
                                onClick = {
                                    viewModel.changeLanguage(
                                        context = context,
                                        language = UserPreferences.Language.Korean
                                    )
                                }
                            ),
                        ),
                    )
                }
            )
        },
        scrollable = false
    ) {
        LoadingOverlay(isLoading || initializingProfile) {
            Column(
                modifier = Modifier
                    .padding(Dimensions.Padding.Medium)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = Dimensions.Padding.SuperHuge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
            ) {
                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = null,
                            indication = ripple(
                                bounded = true
                            )
                        ) {
                            viewModel.openImagePicker()
                        },
                    verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Small),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.profile_picture),
                        style = JAEMTextStyle(
                            MaterialTheme.typography.titleSmall,
                            color = JAEMThemeProvider.current.textSecondary
                        ),
                    )

                    ProfilePicture(
                        modifier = Modifier
                            .size(Dimensions.Size.Huge),
                        profilePicture = profilePicture,
                        showPlaceholder = false
                    )
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = profileName,
                    shape = Dimensions.Shape.Rounded.Small,
                    onValueChange = {
                        viewModel.changeName(it)
                    },
                    textStyle = JAEMTextStyle(
                        MaterialTheme.typography.titleMedium,
                        color = JAEMThemeProvider.current.textPrimary
                    ).copy(
                        fontSize = Dimensions.FontSize.Medium
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.profile_name),
                            style = JAEMTextStyle(
                                MaterialTheme.typography.titleSmall,
                                color = JAEMThemeProvider.current.textSecondary
                            ),
                        )
                    },
                    colors = JAEMTextFieldColors(),
                    singleLine = true,
                    maxLines = 1
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = profileDescription,
                    shape = Dimensions.Shape.Rounded.Small,
                    onValueChange = {
                        viewModel.changeDescription(it)
                    },
                    textStyle = JAEMTextStyle(
                        MaterialTheme.typography.titleMedium,
                        color = JAEMThemeProvider.current.textPrimary
                    ).copy(
                        fontSize = Dimensions.FontSize.Medium
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.profile_description),
                            style = JAEMTextStyle(
                                MaterialTheme.typography.titleSmall,
                                color = JAEMThemeProvider.current.textSecondary
                            ),
                        )
                    },
                    colors = JAEMTextFieldColors(),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.Padding.Small),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
                ) {
                    JAEMButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.edit_message_delivery_server_list),
                        icon = Icons.AutoMirrored.Filled.Message,
                        onClick = {
                            viewModel.openMessageDeliveryDialog()
                        }
                    )
                    JAEMButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.edit_uds_server_list),
                        icon = Icons.Default.People,
                        onClick = {
                            viewModel.openUdsDialog()
                        }
                    )
                }

                JAEMCheckBox(
                    text = stringResource(id = R.string.allow_profile_sharing),
                    checked = allowProfileSharing ?: false,
                    onCheckedChange = {
                        viewModel.changeAllowProfileSharing(it)
                    }
                )
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .padding(
                        start = Dimensions.Padding.Medium,
                        end = Dimensions.Padding.Medium,
                        bottom = Dimensions.Padding.Medium
                    )
                    .border(
                        width = Dimensions.Border.ThinBorder,
                        color = JAEMThemeProvider.current.border,
                        shape = Dimensions.Shape.Rounded.Small
                    ),
                onClick = {
                    viewModel.completeInitialization()
                    navigationViewModel.navigateTo(NavRoute.ChatOverview)
                },
                containerColor = JAEMThemeProvider.current.primary
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            vertical = Dimensions.Padding.Small,
                            horizontal = Dimensions.Padding.Medium
                        ),
                    text = stringResource(R.string.create_profile),
                    style = JAEMTextStyle(
                        MaterialTheme.typography.titleMedium,
                    )
                )
            }
        }
    }

    if (isMessageDeliveryDialogOpen) {
        EditServerListDialog(
            onDismissRequest = {
                viewModel.closeMessageDeliveryDialog()
            },
            serverListType = ServerListType.MESSAGE_DELIVERY,
        )
    }
    if (isUdsDialogOpen) {
        EditServerListDialog(
            onDismissRequest = {
                viewModel.closeUdsDialog()
            },
            serverListType = ServerListType.USER_DISCOVERY,
            joinOrLeaveServers = false,
            onUrlsChanged = { addedUrls, removedUrls ->
                viewModel.setAddedAndRemovedUrls(addedUrls, removedUrls)
            }
        )
    }

    if (imagePickerIsOpen) {
        JAEMPickFileAndCrop(
            onDismiss = {
                viewModel.closeImagePicker()
            },
            selected = { imageBytes ->
                coroutineScope.launch(Dispatchers.IO) {
                    isLoading = true

                    viewModel.changeProfilePicture(imageBytes)

                    viewModel.closeImagePicker()
                    isLoading = false
                }
            }
        )
    }

}