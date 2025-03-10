package de.stubbe.jaem_client.view.screens.uds

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.JameMenuItemModel
import de.stubbe.jaem_client.view.components.JAEMDropMenu
import de.stubbe.jaem_client.view.screens.chat.SearchTopBar
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.NavigationViewModel

@Composable
fun UDSTopBar(
    navigationViewModel: NavigationViewModel,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onOpenEditServerListDialog: () -> Unit
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    SearchTopBar(
        onGoBack = {
            navigationViewModel.goBack()
        },
        searchText = searchText,
        onSearchTextChange = onSearchTextChange,
        title = {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.user_discovery),
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge)
            )
        },
        extraActions = {
            IconButton(onClick = {
                showDropDownMenu = true
            }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_actions_bt),
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
            JAEMDropMenu(
                visible = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false },
                menuItems = listOf(
                    JameMenuItemModel(
                        title = stringResource(R.string.edit_server_list),
                        leadingIcon = Icons.Filled.Edit,
                        onClick = {
                            onOpenEditServerListDialog()
                            showDropDownMenu = false
                        }
                    )
                )
            )
        }
    )
}