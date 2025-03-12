package de.stubbe.jaem_client.view.screens.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.datastore.UserPreferences.Language
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import de.stubbe.jaem_client.model.enums.ServerListType
import de.stubbe.jaem_client.utils.changeAppLanguage
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.components.JAEMButton
import de.stubbe.jaem_client.view.components.JAEMCheckBox
import de.stubbe.jaem_client.view.components.JAEMDropDownMenu
import de.stubbe.jaem_client.view.components.dialogs.ConfirmDialog
import de.stubbe.jaem_client.view.components.dialogs.EditServerListDialog
import de.stubbe.jaem_client.view.components.topbars.TitleWithActionsTopBar
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.drop

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SettingsScreen(
    navigationViewModel: NavigationViewModel
) {
    val context = LocalContext.current

    val viewModel: SettingsViewModel = hiltViewModel()

    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val allowProfileSharing by viewModel.allowProfileSharing.collectAsState()

    val deleteDataDialogIsOpen by viewModel.deleteDataDialogIsOpen.collectAsState()
    val isMessageDeliveryDialogOpen by viewModel.isMessageDeliveryDialogOpen.collectAsState()
    val isUdsDialogOpen by viewModel.isUdsDialogOpen.collectAsState()

    LaunchedEffect(Unit) {
        snapshotFlow { selectedLanguage }
            .drop(1)
            .collect { selectedLanguageState ->
                changeAppLanguage(context, selectedLanguageState)
            }
    }

    ScreenBase(
        topBar = {
            TitleWithActionsTopBar(
                title = stringResource(id = R.string.settings),
                trailingActions = {
                    IconButton(
                        onClick = {
                            navigationViewModel.goBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close_bt),
                            tint = JAEMThemeProvider.current.textPrimary
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = Dimensions.Padding.Medium,
                    horizontal = Dimensions.Padding.Large
                ),
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.theme),
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge).copy(
                    textAlign = TextAlign.Center
                ),
            )
            JAEMDropDownMenu(
                items = listOf(Theme.SYSTEM, Theme.DARK, Theme.LIGHT, Theme.CRYPTO),
                selectedItem = selectedTheme,
                onItemSelected = {
                    viewModel.updateTheme(it)
                },
                getTitle = {
                    when (it) {
                        Theme.SYSTEM -> stringResource(id = R.string.system)
                        Theme.DARK -> stringResource(id = R.string.dark)
                        Theme.LIGHT -> stringResource(id = R.string.light)
                        Theme.CRYPTO -> stringResource(id = R.string.crypto)
                        else -> {
                            ""
                        }
                    }
                },
            )

            Divider(alpha = 0.1f)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.language),
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge).copy(
                    textAlign = TextAlign.Center
                ),
            )
            JAEMDropDownMenu(
                items = listOf(Language.GERMAN, Language.ENGLISH, Language.Russian, Language.Korean),
                selectedItem = selectedLanguage,
                onItemSelected = {
                    viewModel.updateLanguage(it)
                },
                getTitle = {
                    when (it) {
                        Language.GERMAN -> stringResource(id = R.string.german)
                        Language.ENGLISH -> stringResource(id = R.string.english)
                        Language.Russian -> stringResource(id = R.string.russian)
                        Language.Korean -> stringResource(id = R.string.korean)
                        else -> {
                            ""
                        }
                    }
                },
            )

            Divider(alpha = 0.1f)

            JAEMCheckBox(
                text = stringResource(id = R.string.allow_profile_sharing),
                checked = allowProfileSharing ?: false,
                onCheckedChange = {
                    viewModel.updateProfileSharing(it)
                }
            )

            Divider(alpha = 0.1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
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

            Divider(alpha = 0.1f)

            JAEMButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.delete_profile_and_key_data),
            ) {
                viewModel.openDeleteDataDialog()
            }
        }
    }

    if (deleteDataDialogIsOpen) {
        ConfirmDialog(
            icon = Icons.Default.Warning,
            title = stringResource(id = R.string.delete_profile_and_key_data),
            message = stringResource(id = R.string.do_you_really_want_to_delete_profile_and_key_data),
            onDismissRequest = {
                viewModel.closeDeleteDataDialog()
            },
            onConfirmRequest = {
                viewModel.deleteDeviceData(navigationViewModel)
            }
        )
    }

    if (isMessageDeliveryDialogOpen) {
        EditServerListDialog(
            onDismissRequest = {
                viewModel.closeMessageDeliveryDialog()
            },
            serverListType = ServerListType.MESSAGE_DELIVERY
        )
    }
    if (isUdsDialogOpen) {
        EditServerListDialog(
            onDismissRequest = {
                viewModel.closeUdsDialog()
            },
            serverListType = ServerListType.USER_DISCOVERY
        )
    }
}