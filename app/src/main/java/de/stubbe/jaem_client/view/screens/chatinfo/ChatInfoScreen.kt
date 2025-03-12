package de.stubbe.jaem_client.view.screens.chatinfo

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.view.components.Divider
import de.stubbe.jaem_client.view.components.JAEMButton
import de.stubbe.jaem_client.view.components.LoadingIfNull
import de.stubbe.jaem_client.view.components.ShareProfileBottomSheet
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.ShareProfileViewModel
import de.stubbe.jaem_client.viewmodel.SharedChatViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChatInfoScreen(
    navigationViewModel: NavigationViewModel,
    sharedChatViewModel: SharedChatViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    val shareProfileViewModel: ShareProfileViewModel = hiltViewModel()

    val chat by sharedChatViewModel.chat.collectAsState()
    val profile by sharedChatViewModel.profile.collectAsState()

    ScreenBase(
        topBar = {
            ChatInfoTopBar(
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                profilePicture = chat?.profilePicture,
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
            LoadingIfNull(profile) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.Padding.Medium),
                    text = chat?.name ?: "",
                    style = JAEMTextStyle(MaterialTheme.typography.headlineMedium).copy(
                        textAlign = TextAlign.Center
                    )
                )

                ProfileMainActions(
                    onShareClick = {
                        if (profile != null) {
                            shareProfileViewModel.openShareProfileBottomSheet(profile!!)
                        }
                    },
                    onSearchClick = {
                        navigationViewModel.goBack<NavRoute.ChatMessages> {
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
        }

        ShareProfileBottomSheet(
            onClose = {
                shareProfileViewModel.closeShareProfileBottomSheet()
            },
            shareProfileViewModel = shareProfileViewModel
        )
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
    JAEMButton(
        modifier = Modifier
            .weight(1f)
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Small
            ),
        text = text,
        icon = icon,
        onClick = onClick
    )
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