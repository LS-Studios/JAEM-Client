package de.stubbe.jaem_client.view.screens.chatoverview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.screens.chat.SearchTopBar
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.view.variables.RaviPrakash

@Composable
fun ChatOverviewTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
) {
    SearchTopBar(
        onGoBack = null,
        searchText = searchText,
        onSearchTextChange = onSearchTextChange,
        title = {
            // Name der App
            Text(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = 12f
                    },
                text = stringResource(R.string.app_name),
                style = JAEMTextStyle(MaterialTheme.typography.headlineLarge, fontFamily = RaviPrakash),
            )
        },
        extraActions = {
            // Einstellungen-Action
            IconButton(onClick = {

            }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_bt),
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
        }
    )
}