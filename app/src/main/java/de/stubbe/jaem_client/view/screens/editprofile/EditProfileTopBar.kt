package de.stubbe.jaem_client.view.screens.editprofile

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChatTopBar(
    createProfile: Boolean,
    oClose: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(if (createProfile) R.string.create_profile else R.string.edit_profile),
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge),
            )
        },
        actions = {
            IconButton(
                onClick = oClose
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_bt),
                    tint = JAEMThemeProvider.current.textPrimary
                )
            }
        },
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = JAEMThemeProvider.current.background),
    )
}