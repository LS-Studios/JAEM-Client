package de.stubbe.jaem_client.view.screens.profile

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.utils.addViewModelExtras
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.components.ShareProfileBottomSheet
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMTextStyle
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.AppViewModelProvider
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.ProfileViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProfileScreen(
    navigationViewModel: NavigationViewModel,
    profileInfoArguments: NavRoute.ProfileInfo,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    viewModel: ProfileViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        extras = addViewModelExtras {
            set(ProfileViewModel.PROFILE_ID_KEY, profileInfoArguments.profileId)
        }
    )
) {
    val profile by viewModel.profile.collectAsState()
    val isShareProfileBottomSheetVisible by viewModel.isShareProfileBottomSheetVisible.collectAsState()

    ScreenBase(
        topBar = {
            ProfileTopBar(
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
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
                style = JAEMTextStyle(MaterialTheme.typography.headlineMedium).copy(
                    textAlign = TextAlign.Center
                )
            )

            ProfileMainActions(
                onShareClick = {
                    viewModel.openShareProfileBottomSheet()
                },
                onSearchClick = {
                    navigationViewModel.goBack<NavRoute.Chat> {
                        copy(searchEnabled = true)
                    }
                }
            )

            Divider()

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.Padding.Medium),
                text = profile?.description ?: "",
                style = JAEMTextStyle(MaterialTheme.typography.titleLarge).copy(
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

        if (profile != null) {
            ShareProfileBottomSheet(
                isVisible = isShareProfileBottomSheetVisible,
                onClose = {
                    viewModel.closeShareProfileBottomSheet()
                },
                profile = profile!!,
                profileViewModel = viewModel
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
            icon = Icons.Default.Key,
            text = stringResource(R.string.key_data),
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
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Small
            ),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = JAEMThemeProvider.current.background
        ),
        shape = Dimensions.Shape.Rounded.Small,
        border = BorderStroke(
            width = Dimensions.Border.ThinBorder,
            color = JAEMThemeProvider.current.border
        )
    ) {
        Column(
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
                style = JAEMTextStyle(MaterialTheme.typography.bodyMedium)
            )
        }
    }
}

@Composable
private fun ProfileActions(
    profile: ProfilePresentationModel?,
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
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = Dimensions.Padding.Medium,
        ),
        shape = Dimensions.Shape.Rectangle,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = JAEMThemeProvider.current.background
        ),
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
            style = JAEMTextStyle(
                MaterialTheme.typography.titleMedium,
                color = JAEMThemeProvider.current.error
            ).copy(
                fontSize = Dimensions.FontSize.Medium
            )
        )
    }
}