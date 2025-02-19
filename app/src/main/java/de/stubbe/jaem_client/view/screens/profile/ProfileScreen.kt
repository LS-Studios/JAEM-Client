package de.stubbe.jaem_client.view.screens.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.utils.addViewModelExtras
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.view.variables.JaemTextStyle
import de.stubbe.jaem_client.viewmodel.AppViewModelProvider
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navigationViewModel: NavigationViewModel,
    profileInfoArguments: NavRoute.ProfileInfo,
    viewModel: ProfileViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        extras = addViewModelExtras {
            set(ProfileViewModel.PROFILE_ID_KEY, profileInfoArguments.profileId)
        }
    )
) {
    val profile by viewModel.profile.collectAsState()

    ScreenBase(
        topBar = {
            ProfileTopBar(
                profile = profile,
                onClose = {
                    navigationViewModel.goBack()
                }
            )
        },
        useDivider = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimensions.Padding.Medium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.Padding.Medium),
                text = profile?.name ?: "",
                style = JaemTextStyle(MaterialTheme.typography.headlineMedium).copy(
                    textAlign = TextAlign.Center
                )
            )

            ProfileMainActions(
                onShareClick = { },
                onSearchClick = { }
            )

            Divider()

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.Padding.Medium),
                text = profile?.description ?: "",
                style = JaemTextStyle(MaterialTheme.typography.titleLarge).copy(
                    fontSize = Dimensions.FontSize.Medium,
                    textAlign = TextAlign.Center
                )
            )

            Divider()

            ProfileActions(
                profile = profile,
                onDelete = { },
                onBlock = { }
            )
        }
    }
}

@Composable
private fun ProfileMainActions(
    onShareClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.Medium),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium)
    ) {
        ProfileMainActionButton(
            icon = Icons.Default.Share,
            text = stringResource(R.string.share),
            onClick = onShareClick
        )
        ProfileMainActionButton(
            icon = Icons.Default.Search,
            text = stringResource(R.string.search),
            onClick = onSearchClick
        )
    }
}

@Composable
private fun RowScope.ProfileMainActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(Dimensions.Shape.Rounded.Small)
            .clickable { onClick() }
            .border(
                width = Dimensions.Border.ThinBorder,
                color = JAEMThemeProvider.current.border,
                shape = Dimensions.Shape.Rounded.Small
            )
            .padding(Dimensions.Padding.Small),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Tiny),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = JAEMThemeProvider.current.textPrimary
        )
        Text(
            text = text,
            style = JaemTextStyle(MaterialTheme.typography.bodyMedium)
        )
    }
}

@Composable
private fun ProfileActions(
    profile: ProfileModel?,
    onDelete: () -> Unit,
    onBlock: () -> Unit
) {
    Column(
        modifier = Modifier,
    ) {
        ProfileActionButton(
            icon = Icons.Default.Delete,
            text = stringResource(R.string.delete, profile?.name ?: ""),
            onClick = onDelete
        )
        ProfileActionButton(
            icon = Icons.Default.Block,
            text = stringResource(R.string.block, profile?.name ?: ""),
            onClick = onBlock
        )
    }
}

@Composable
private fun ProfileActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Small
            ),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = Dimensions.Padding.Small),
            imageVector = icon,
            contentDescription = text,
            tint = JAEMThemeProvider.current.error
        )
        Text(
            text = text,
            style = JaemTextStyle(
                MaterialTheme.typography.titleMedium,
                color = JAEMThemeProvider.current.error
            ).copy(
                fontSize = Dimensions.FontSize.Medium
            )
        )
    }
}